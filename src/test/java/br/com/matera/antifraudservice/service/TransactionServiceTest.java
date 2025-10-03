package br.com.matera.antifraudservice.service;

import br.com.matera.antifraudservice.client.AccountProxy;
import br.com.matera.antifraudservice.dto.*;
import br.com.matera.antifraudservice.entity.TransactionEntity;
import br.com.matera.antifraudservice.enums.FraudReason;
import br.com.matera.antifraudservice.enums.FraudStatus;
import br.com.matera.antifraudservice.enums.TransactionType;
import br.com.matera.antifraudservice.exceptions.TransactionNotFoundException;
import br.com.matera.antifraudservice.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @InjectMocks
    TransactionService service;

    @Mock
    FraudAnalysisService fraudAnalysisService;

    @Mock
    AccountProxy accountProxy;

    @Mock
    TransactionRepository transactionRepository;

    @Test
    void analyzeNewTransaction(){
        when(accountProxy.getAccountData(any())).thenReturn(getAccountMock());
        when(fraudAnalysisService.analyze(any(TransactionDTO.class), any(AccountDTO.class)))
                .thenReturn(Optional.empty());

        service.analyzeNewTransaction(getTransactionMock());

        verify(fraudAnalysisService, never()).publishFraudReport(any());
    }

    @Test
    void analyzeNewTransaction_SuspeitaFraude(){
        when(accountProxy.getAccountData(any())).thenReturn(getAccountMock());
        FraudAlertDTO value = new FraudAlertDTO();
        value.setMotivo(FraudReason.EXCEEDED_TRANSACTION_LIMIT);
        when(fraudAnalysisService.analyze(any(TransactionDTO.class), any(AccountDTO.class)))
                .thenReturn(Optional.of(value));

        service.analyzeNewTransaction(getTransactionMock());

        verify(fraudAnalysisService, times(1)).publishFraudReport(any());
    }

    @Test
    void getTransactionStatus(){
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setStatus(FraudStatus.APROVADO);
        when(transactionRepository.findById(any())).thenReturn(Optional.of(transactionEntity));

        FraudStatus transactionStatus = service.getTransactionStatus("1");

        assertEquals(FraudStatus.APROVADO, transactionStatus);
    }

    @Test
    void getTransactionStatus_NaoEncontrado(){
        when(transactionRepository.findById(any())).thenReturn(Optional.empty());


        TransactionNotFoundException exception = assertThrows(TransactionNotFoundException.class, () -> {
            service.getTransactionStatus("1");
        });

        assertEquals("Transação com id 1 não encontrado.", exception.getMessage());
    }

    @Test
    void getSuspiciousTransaction(){
        ArrayList<TransactionEntity> transactionEntities = new ArrayList<>();
        transactionEntities.add(getEntityMock());
        when(transactionRepository.findWithFilter(any(), any(), any())).thenReturn(transactionEntities);

        SuspicionFilterDTO filterMock = getFilterMock();

        List<TransactionResponseDTO> suspiciousTransaction = service.getSuspiciousTransaction(filterMock);

        assertFalse(suspiciousTransaction.isEmpty());
        assertEquals(filterMock.getDataFinal().atTime(LocalTime.MAX), filterMock.getDataFinalDateTime());
        assertEquals(filterMock.getDataInicial().atStartOfDay(), filterMock.getDataInicialDateTime());

    }

    @Test
    void updateStatus(){
        when(transactionRepository.findById(any())).
                thenReturn(Optional.of(getEntityMock()));

        UpdateAnalysisStatusDTO request = new UpdateAnalysisStatusDTO();
        request.setStatus(FraudStatus.APROVADO);
        service.updateStatus("1", request);

        verify(transactionRepository, times(1)).findById(any());
        verify(transactionRepository, times(1)).save(any());
    }

    @Test
    void updateStatus_NotFound(){
        when(transactionRepository.findById(any())).
                thenReturn(Optional.empty());

        UpdateAnalysisStatusDTO request = new UpdateAnalysisStatusDTO();
        request.setStatus(FraudStatus.APROVADO);

        TransactionNotFoundException exception = assertThrows(TransactionNotFoundException.class, () -> {
            service.updateStatus("1", request);
        });

        assertEquals("Transação com id 1 não encontrado.", exception.getMessage());
        verify(transactionRepository, times(1)).findById(any());
        verify(transactionRepository, never()).save(any());

    }

    private TransactionEntity getEntityMock(){
        TransactionEntity entity = new TransactionEntity();
        entity.setIdConta("1");
        entity.setIdTransacao("1");
        entity.setReason(FraudReason.EXCEEDED_TRANSACTION_LIMIT);
        entity.setStatus(FraudStatus.SUSPEITA_DE_FRAUDE);
        entity.setTipo(TransactionType.DEBITO);
        entity.setDataHoraPersistencia(LocalDateTime.now());
        entity.setManualAnalysis(true);
        return entity;
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
        account.setValorLimiteParaDebito(new BigDecimal("500"));
        account.setValorLimiteParaCredito(new BigDecimal("500"));
        account.setContaAtiva(true);
        return account;
    }

    private SuspicionFilterDTO getFilterMock(){
        SuspicionFilterDTO filter = new SuspicionFilterDTO();
        filter.setIdConta("1");
        filter.setDataInicial(LocalDate.now());
        filter.setDataFinal(LocalDate.now());
        return filter;
    }

}