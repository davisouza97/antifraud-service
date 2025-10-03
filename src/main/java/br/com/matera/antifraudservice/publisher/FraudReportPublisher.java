package br.com.matera.antifraudservice.publisher;

import br.com.matera.antifraudservice.dto.FraudAlertDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class FraudReportPublisher {

    private static final Logger log = LoggerFactory.getLogger(FraudReportPublisher.class);

    @Value("${topics.fraud-notification}")
    private String fraudNotification;

    KafkaTemplate<String, String> kafkaTemplate;

    ObjectMapper mapper;

    public FraudReportPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper mapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = mapper;
    }

    public void publishFraudSuspicion(FraudAlertDTO fraudAlert) {
        log.info("Publicando alerta de fraude para transação " + fraudAlert.getIdTransacao());
        String message = null;
        try {
            message = mapper.writeValueAsString(fraudAlert);
        } catch (JsonProcessingException e) {
            log.error("Erro ao transformar mensagem e texto para ser publicada.");
            throw new RuntimeException(e);
        }
        kafkaTemplate.send(fraudNotification, message);
        log.info("Alerta de fraude para transação " + fraudAlert.getIdTransacao() + " efetuada com sucesso.");
    }

}
