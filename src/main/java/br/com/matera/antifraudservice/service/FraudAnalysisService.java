package br.com.matera.antifraudservice.service;

import br.com.matera.antifraudservice.dto.AccountDTO;
import br.com.matera.antifraudservice.dto.FraudAlertDTO;
import br.com.matera.antifraudservice.dto.TransactionDTO;
import br.com.matera.antifraudservice.entity.TransactionEntity;
import br.com.matera.antifraudservice.enums.FraudReason;
import br.com.matera.antifraudservice.enums.FraudStatus;
import br.com.matera.antifraudservice.enums.TransactionType;
import br.com.matera.antifraudservice.publisher.FraudReportPublisher;
import br.com.matera.antifraudservice.repository.TransactionRepository;
import br.com.matera.antifraudservice.utils.FeatureFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FraudAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(FraudAnalysisService.class);

    private final FraudReportPublisher fraudReportPublisher;

    private final TransactionRepository transactionRepository;

    private final RedisTemplate<String, String> redisTemplate;

    private final FeatureFlag featureFlag;

    public FraudAnalysisService( FraudReportPublisher fraudReportPublisher,
                                TransactionRepository transactionRepository, RedisTemplate<String, String> redisTemplate, FeatureFlag featureFlag) {
        this.fraudReportPublisher = fraudReportPublisher;
        this.transactionRepository = transactionRepository;
        this.redisTemplate = redisTemplate;
        this.featureFlag = featureFlag;
    }

    public Optional<FraudAlertDTO> analyze(TransactionDTO transaction, AccountDTO account){
        log.info("Inicinado análise da transação: " + transaction.getIdTransacao());

        FraudAlertDTO fraudAlertDTO = FraudAlertDTO.builder()
                .idConta(account.getIdConta())
                .idTransacao(transaction.getIdTransacao())
                .status(FraudStatus.SUSPEITA_DE_FRAUDE)
                .dataHoraAnalise(LocalDateTime.now())
                .build();

        if(!account.isContaAtiva()){
            log.info("Conta " + account.getIdConta() + " inativada, transação suspeita de fraude.");
            fraudAlertDTO.setMotivo(FraudReason.ACCOUNT_INACTIVE);

            return Optional.of(fraudAlertDTO);
        }

        if(validateLimitValueForTransaction(transaction, account)){
            log.info("Valor da transação" + transaction.getIdTransacao() + " é maior do que o limite do cliente para esse tipo de transação.");
            fraudAlertDTO.setMotivo(FraudReason.EXCEEDED_TRANSACTION_LIMIT);

            saveTransactionToVerifyFutureSameTypeTransactions(transaction);

            return Optional.of(fraudAlertDTO);
        }

        if(!validateSuspectTransactionsWithoutManualReview(transaction)) {
            log.info("Existe ao menos uma transação do mesmo cliente suspeita de fraude sem análise manual nos últimos " + featureFlag.getTransactionLookbackDays() + " dia(s).");
            fraudAlertDTO.setMotivo(FraudReason.SUSPECT_TRANSACTION_INTERVAL_WITHOUT_REVIEW);
            return Optional.of(fraudAlertDTO);
        }

        if(validateSuspiciousTransactionsInSameInterval(transaction)){
            log.info("Existem muitas transações do mesmo tipo que excederam o limite realizadas em um curto intervalo de tempo.");
            fraudAlertDTO.setMotivo(FraudReason.SUSPECT_TRANSACTION_INTERVAL);
            return Optional.of(fraudAlertDTO);
        }

        return Optional.empty();
    }

    public void publishFraudReport(FraudAlertDTO fraudAlertDTO) {
        log.info("Transação " + fraudAlertDTO.getIdTransacao() + " foi identificada como possivelmente fraudulenta, publicando mensagem no tópico correspondente.");
        fraudReportPublisher.publishFraudSuspicion(fraudAlertDTO);
    }

    private boolean validateSuspectTransactionsWithoutManualReview(TransactionDTO transaction) {
        log.debug("Verificando transações do mesmo cliente com status suspeito de fraude e sem análise manual nos últimos " + featureFlag.getTransactionLookbackDays() + " dia(s).");
        int days = featureFlag.getTransactionLookbackDays();
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(days);
        List<TransactionEntity> transactionsWithoutManualAnalysis =  transactionRepository
                .findTransactionsWithoutManualAnalysis(transaction.getIdConta(), startDate, endDate);

        if(transactionsWithoutManualAnalysis.isEmpty()){
            log.debug("Nenhuma transação suspeita encontrada.");
            return true;
        }
        log.debug("Foram encontrada(s): " + transactionsWithoutManualAnalysis.size() + " transações suspeitas, portanto essa também será colocada como suspeita para análise manual.");
        return false;

    }

    private boolean validateLimitValueForTransaction(TransactionDTO transaction, AccountDTO account) {
        BigDecimal limitValue;
        if(TransactionType.CREDITO.equals(transaction.getTipo())){
            limitValue = account.getValorLimiteParaCredito();
        } else {
            limitValue = account.getValorLimiteParaDebito();
        }
        return transaction.getValor().compareTo(limitValue) > 0;
    }

    private void saveTransactionToVerifyFutureSameTypeTransactions(TransactionDTO transaction){
        String key = transaction.getIdConta() + "_" + transaction.getTipo().getName();
        String now = String.valueOf(Instant.now().toEpochMilli());

        redisTemplate.opsForList().rightPush(key, now);
        redisTemplate.expire(key, Duration.ofMinutes(featureFlag.getSameTypeTransactionWindowInMinutes()));
    }

    private boolean validateSuspiciousTransactionsInSameInterval(TransactionDTO transaction) {
        String key = transaction.getIdConta() + "_" + transaction.getTipo().getName();

        List<String> timestamps = redisTemplate.opsForList().range(key, 0, -1);
        return timestamps != null && timestamps.size() > featureFlag.getMaxSameTypeTransactions();
    }



}
