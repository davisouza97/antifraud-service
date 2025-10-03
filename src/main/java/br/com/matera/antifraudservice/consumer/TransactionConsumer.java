package br.com.matera.antifraudservice.consumer;


import br.com.matera.antifraudservice.dto.TransactionDTO;
import br.com.matera.antifraudservice.exceptions.PayloadConvertException;
import br.com.matera.antifraudservice.service.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class TransactionConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransactionConsumer.class);

    private final ObjectMapper mapper;

    private final TransactionService transactionService;

    private final RedisTemplate<String, String> redisTemplate;

    private static final String PROCESSED_KEY_PREFIX = "transaction:processed:";

    public TransactionConsumer(TransactionService transactionService, ObjectMapper mapper, RedisTemplate<String, String> redisTemplate) {
        this.transactionService = transactionService;
        this.mapper = mapper;
        this.redisTemplate = redisTemplate;
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

        String redisKey = PROCESSED_KEY_PREFIX + transaction.getIdTransacao();

        Boolean firstTime = redisTemplate.opsForValue()
                .setIfAbsent(redisKey, "0", Duration.ofHours(1));

        if (Boolean.FALSE.equals(firstTime)) {
            log.warn("Transação {} já processada, ignorando.", transaction.getIdTransacao());
            return;
        }

        transactionService.analyzeNewTransaction(transaction);

        log.info("Transação {} processada com sucesso.", transaction.getIdTransacao());

    }



}
