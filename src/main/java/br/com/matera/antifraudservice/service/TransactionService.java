package br.com.matera.antifraudservice.service;

import br.com.matera.antifraudservice.client.AccountProxy;
import br.com.matera.antifraudservice.dto.*;
import br.com.matera.antifraudservice.entity.TransactionEntity;
import br.com.matera.antifraudservice.enums.FraudStatus;
import br.com.matera.antifraudservice.exceptions.TransactionNotFoundException;
import br.com.matera.antifraudservice.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final FraudAnalysisService fraudAnalysisService;

    private final AccountProxy accountProxy;

    private final TransactionRepository transactionRepository;

    public TransactionService(FraudAnalysisService fraudAnalysisService, AccountProxy accountProxy, TransactionRepository transactionRepository) {
        this.fraudAnalysisService = fraudAnalysisService;
        this.accountProxy = accountProxy;
        this.transactionRepository = transactionRepository;
    }

    public void analyzeNewTransaction(TransactionDTO transaction){
        log.info("Inicinado análise da transação: " + transaction.getIdTransacao());

        AccountDTO account = accountProxy.getAccountData(transaction.getIdConta());

        TransactionEntity transactionEntity = TransactionEntity.transactionEntityFromDTO(transaction);
        transactionEntity.setStatus(FraudStatus.APROVADO);

        Optional<FraudAlertDTO> fraudAlertOptional = fraudAnalysisService.analyze(transaction, account);

        fraudAlertOptional.ifPresent(fraudAlert -> {
            log.info("Transação " + transaction.getIdTransacao() + " suspeito de fraude, publicando mensagem sobre a suspeita");
            log.debug("Transação " + transaction.getIdTransacao() + " - " +fraudAlert.getMotivo().getDescription());
            transactionEntity.setStatus(FraudStatus.SUSPEITA_DE_FRAUDE);
            transactionEntity.setReason(fraudAlert.getMotivo());
            fraudAnalysisService.publishFraudReport(fraudAlert);
        });

        persistTransaction(transactionEntity);
    }

    private void persistTransaction(TransactionEntity transactionEntity) {
        log.info("Salvando transação " + transactionEntity.getIdTransacao() + " com o status: " + transactionEntity.getStatus().name());
        transactionRepository.save(transactionEntity);
    }

    public FraudStatus getTransactionStatus(String idTransaction) {
        Optional<TransactionEntity> transaction = transactionRepository.findById(idTransaction);
        return transaction.orElseThrow(() -> new TransactionNotFoundException("Transação com id " + idTransaction + " não encontrado.")).getStatus();
    }

    public List<TransactionResponseDTO> getSuspiciousTransaction(SuspicionFilterDTO filter) {
        List<TransactionEntity> transactions = transactionRepository.findWithFilter(filter.getIdConta(), filter.getDataInicialDateTime(), filter.getDataFinalDateTime());

        return transactions.stream().map(TransactionResponseDTO::getTransactionResponseDTOFromEntity)
                .collect(Collectors.toList());
    }


    public void updateStatus(String idTransaction, UpdateAnalysisStatusDTO request) {
        TransactionEntity transactionEntity = transactionRepository.findById(idTransaction)
                .orElseThrow(() -> new TransactionNotFoundException("Transação com id " + idTransaction + " não encontrado."));

        transactionEntity.setManualAnalysis(true);
        transactionEntity.setStatus(request.getStatus());

        transactionRepository.save(transactionEntity);

    }
}
