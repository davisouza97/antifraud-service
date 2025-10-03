package br.com.matera.antifraudservice.consumer;


import br.com.matera.antifraudservice.dto.TransactionDTO;
import br.com.matera.antifraudservice.exceptions.PayloadConvertException;
import br.com.matera.antifraudservice.service.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TransactionConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransactionConsumer.class);

    ObjectMapper mapper;

    TransactionService transactionService;

    public TransactionConsumer(TransactionService transactionService, ObjectMapper mapper) {
        this.transactionService = transactionService;
        this.mapper = mapper;
    }

    @KafkaListener(topics = "${topics.transactions}", groupId = "${spring.kafka.consumer.group-id}")
    public void transactionListener(String message){
        log.info("Mensagem recebida, iniciando validações.");
        log.debug("Mensagem recebida: " + message);

        TransactionDTO transaction;
        try {
            transaction = mapper.readValue(message, TransactionDTO.class);
            log.debug("Mensagem convertida para objeto");
        } catch (JsonProcessingException e) {
            log.error("Erro ao converter mensagem para objeto");
            throw new PayloadConvertException("Erro ao converter mensagem para objeto", e);
        }

        transactionService.analyzeNewTransaction(transaction);

    }



}
