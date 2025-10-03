package br.com.matera.antifraudservice.publisher;

import br.com.matera.antifraudservice.dto.FraudAlertDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FraudReportPublisherTest {

    @InjectMocks
    FraudReportPublisher publisher;

    @Mock
    KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    ObjectMapper mapper;

    @Test
    void publishFraudSuspicion() throws JsonProcessingException {
        when(mapper.writeValueAsString(any())).thenReturn("{body: 123}");

        publisher.publishFraudSuspicion(new FraudAlertDTO());

        verify(kafkaTemplate, times(1)).send(any(), any());
    }

    @Test
    void publishFraudSuspicion_Exception() throws JsonProcessingException {
        String erroMockado = "Erro mockado";
        doThrow(new com.fasterxml.jackson.databind.JsonMappingException(null, erroMockado))
                .when(mapper).writeValueAsString(any());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            publisher.publishFraudSuspicion(new FraudAlertDTO());
        });

        assertTrue(exception.getMessage().contains(erroMockado));
        verify(kafkaTemplate, never()).send(any(), any());
    }

}