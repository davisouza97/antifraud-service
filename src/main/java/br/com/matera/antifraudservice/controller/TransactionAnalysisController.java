package br.com.matera.antifraudservice.controller;


import br.com.matera.antifraudservice.dto.SuspicionFilterDTO;
import br.com.matera.antifraudservice.dto.TransactionResponseDTO;
import br.com.matera.antifraudservice.dto.UpdateAnalysisStatusDTO;
import br.com.matera.antifraudservice.enums.FraudStatus;
import br.com.matera.antifraudservice.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/analises")
public class TransactionAnalysisController {

    private static final Logger log = LoggerFactory.getLogger(TransactionAnalysisController.class);

    private final TransactionService transactionService;

    public TransactionAnalysisController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{idTransaction}")
    public ResponseEntity<FraudStatus> getTransactionStatus(@PathVariable String idTransaction){
        log.info("Recuperando status da transação com id " + idTransaction);
        FraudStatus transactionStatus = transactionService.getTransactionStatus(idTransaction);
        log.info("Status da transação " + idTransaction + ": " + transactionStatus.name());
        return ResponseEntity.ok().body(transactionStatus);
    }

    @GetMapping("/suspeitas")
    public ResponseEntity<List<TransactionResponseDTO>> getSuspicionTransactions(@ModelAttribute SuspicionFilterDTO filter){
        log.info("Recuperando transações por filtro");
        List<TransactionResponseDTO> transactions = transactionService.getSuspiciousTransaction(filter);
        return ResponseEntity.ok().body(transactions);
    }

    @PatchMapping("/{idTransaction}")
    public ResponseEntity<Object> updateStatus(@PathVariable String idTransaction, @RequestBody UpdateAnalysisStatusDTO request){
        log.info("Recuperando transações por filtro");
        transactionService.updateStatus(idTransaction, request);
        return ResponseEntity.noContent().build();
    }
}
