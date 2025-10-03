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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FraudAnalysisServiceTest {

    private static final BigDecimal VALOR_LIMITE_PARA_DEBITO = new BigDecimal("500");
    private static final BigDecimal VALOR_LIMITE_PARA_CREDITO = new BigDecimal("1000");

    @InjectMocks
    FraudAnalysisService service;

    @Mock
    TransactionRepository transactionRepository;

    @Mock
    RedisTemplate<String, String> redisTemplate;

    @Mock
    ListOperations<String, String> listOperations;

    @Mock
    FraudReportPublisher fraudReportPublisher;

    @Mock
    FeatureFlag featureFlag;

    @Test
    void analyze() {

        when(transactionRepository.findTransactionsWithoutManualAnalysis(anyString(),
                any(LocalDateTime.class),any(LocalDateTime.class))).thenReturn(new ArrayList<>());

        List<String> mockTimestamps = List.of();
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        when(listOperations.range(anyString(), anyLong(), anyLong())).thenReturn(mockTimestamps);

        TransactionDTO transactionMock = getTransactionMock();
        AccountDTO accountMock = getAccountMock();

        Optional<FraudAlertDTO> response = service.analyze(transactionMock, accountMock);

        assertTrue(response.isEmpty());
    }

    @Test
    void analyze_contaInativa() {
        TransactionDTO transactionMock = getTransactionMock();
        AccountDTO accountMock = getAccountMock();
        accountMock.setContaAtiva(false);

        Optional<FraudAlertDTO> response = service.analyze(transactionMock, accountMock);

        assertTrue(response.isPresent());
        FraudAlertDTO fraudAlertDTO = response.get();
        assertEquals(FraudStatus.SUSPEITA_DE_FRAUDE, fraudAlertDTO.getStatus());
        assertEquals(FraudReason.ACCOUNT_INACTIVE, fraudAlertDTO.getMotivo());
    }

    @Test
    void analyze_ValorLimiteDebito() {
        when(redisTemplate.opsForList()).thenReturn(listOperations);

        TransactionDTO transactionMock = getTransactionMock();
        transactionMock.setTipo(TransactionType.DEBITO);
        transactionMock.setValor(VALOR_LIMITE_PARA_DEBITO.add(BigDecimal.ONE));
        AccountDTO accountMock = getAccountMock();

        Optional<FraudAlertDTO> response = service.analyze(transactionMock, accountMock);

        assertTrue(response.isPresent());
        FraudAlertDTO fraudAlertDTO = response.get();
        assertEquals(FraudStatus.SUSPEITA_DE_FRAUDE, fraudAlertDTO.getStatus());
        assertEquals(FraudReason.EXCEEDED_TRANSACTION_LIMIT, fraudAlertDTO.getMotivo());
    }

    @Test
    void analyze_ValorLimiteCredito() {
        when(redisTemplate.opsForList()).thenReturn(listOperations);

        TransactionDTO transactionMock = getTransactionMock();
        transactionMock.setTipo(TransactionType.CREDITO);
        transactionMock.setValor(VALOR_LIMITE_PARA_CREDITO.add(BigDecimal.ONE));
        AccountDTO accountMock = getAccountMock();

        Optional<FraudAlertDTO> response = service.analyze(transactionMock, accountMock);

        assertTrue(response.isPresent());
        FraudAlertDTO fraudAlertDTO = response.get();
        assertEquals(FraudStatus.SUSPEITA_DE_FRAUDE, fraudAlertDTO.getStatus());
        assertEquals(FraudReason.EXCEEDED_TRANSACTION_LIMIT, fraudAlertDTO.getMotivo());
    }

    @Test
    void analyze_TransacoesSuspeitasAnteriores() {
        ArrayList<TransactionEntity> transacoesSuspeitas = new ArrayList<>();
        transacoesSuspeitas.add(new TransactionEntity());
        when(transactionRepository.findTransactionsWithoutManualAnalysis(anyString(),
                any(LocalDateTime.class),any(LocalDateTime.class))).thenReturn(transacoesSuspeitas);

        TransactionDTO transactionMock = getTransactionMock();
        AccountDTO accountMock = getAccountMock();

        Optional<FraudAlertDTO> response = service.analyze(transactionMock, accountMock);

        assertTrue(response.isPresent());
        FraudAlertDTO fraudAlertDTO = response.get();
        assertEquals(FraudStatus.SUSPEITA_DE_FRAUDE, fraudAlertDTO.getStatus());
        assertEquals(FraudReason.SUSPECT_TRANSACTION_INTERVAL_WITHOUT_REVIEW, fraudAlertDTO.getMotivo());
    }

    @Test
    void analyze_TransacoesSuspeitasMesmoIntervalo() {
        when(transactionRepository.findTransactionsWithoutManualAnalysis(anyString(),
                any(LocalDateTime.class),any(LocalDateTime.class))).thenReturn(new ArrayList<>());

        List<String> mockTimestamps = Arrays.asList("2025-10-03T10:00", "2025-10-03T11:00");
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        when(listOperations.range(anyString(), anyLong(), anyLong())).thenReturn(mockTimestamps);

        TransactionDTO transactionMock = getTransactionMock();
        AccountDTO accountMock = getAccountMock();

        Optional<FraudAlertDTO> response = service.analyze(transactionMock, accountMock);

        assertTrue(response.isPresent());
        FraudAlertDTO fraudAlertDTO = response.get();
        assertEquals(FraudStatus.SUSPEITA_DE_FRAUDE, fraudAlertDTO.getStatus());
        assertEquals(FraudReason.SUSPECT_TRANSACTION_INTERVAL, fraudAlertDTO.getMotivo());
    }

    @Test
    void publishFraudReport() {
        FraudAlertDTO fraudAlertDTO = new FraudAlertDTO();
        service.publishFraudReport(fraudAlertDTO);
    }

    private TransactionDTO getTransactionMock(){
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setIdConta("idContaMock");
        transactionDTO.setIdTransacao("idTransacaoMock");
        transactionDTO.setTipo(TransactionType.DEBITO);
        transactionDTO.setValor(BigDecimal.TEN);
        transactionDTO.setDataHora(LocalDateTime.now());
        return transactionDTO;
    }

    private AccountDTO getAccountMock() {
        AccountDTO account = new AccountDTO();
        account.setIdConta("idContaMock");
        account.setValorLimiteParaDebito(VALOR_LIMITE_PARA_DEBITO);
        account.setValorLimiteParaCredito(VALOR_LIMITE_PARA_CREDITO);
        account.setContaAtiva(true);
        return account;
    }
}