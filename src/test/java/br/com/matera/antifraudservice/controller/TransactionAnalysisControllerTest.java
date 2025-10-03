package br.com.matera.antifraudservice.controller;

import br.com.matera.antifraudservice.dto.SuspicionFilterDTO;
import br.com.matera.antifraudservice.dto.TransactionResponseDTO;
import br.com.matera.antifraudservice.dto.UpdateAnalysisStatusDTO;
import br.com.matera.antifraudservice.enums.FraudStatus;
import br.com.matera.antifraudservice.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionAnalysisControllerTest {

    @InjectMocks
    private TransactionAnalysisController controller;

    @Mock
    TransactionService transactionService;

    @Test
    void getTransactionStatus(){
        when(transactionService.getTransactionStatus(any())).thenReturn(FraudStatus.APROVADO);

        ResponseEntity<FraudStatus> responseEntity = controller.getTransactionStatus("1");

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(FraudStatus.APROVADO, responseEntity.getBody());
    }

    @Test
    void getSuspicionTransactions(){
        when(transactionService.getSuspiciousTransaction(any())).thenReturn(new ArrayList<>());

        ResponseEntity<List<TransactionResponseDTO>> responseEntity = controller.getSuspicionTransactions(new SuspicionFilterDTO());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(0, responseEntity.getBody().size());
    }

    @Test
    void updateStatus(){
        UpdateAnalysisStatusDTO request = new UpdateAnalysisStatusDTO();
        request.setStatus(FraudStatus.APROVADO);
        ResponseEntity<Object> responseEntity = controller.updateStatus("", request);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

}