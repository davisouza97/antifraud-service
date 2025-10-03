package br.com.matera.antifraudservice.consumer;

import br.com.matera.antifraudservice.dto.TransactionDTO;
import br.com.matera.antifraudservice.exceptions.PayloadConvertException;
import br.com.matera.antifraudservice.service.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionConsumerTest {

    @InjectMocks
    TransactionConsumer consumer;

    @Mock
    ObjectMapper mapper;

    @Mock
    TransactionService transactionService;

    @Mock
    RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private static final String PROCESSED_KEY_PREFIX = "transaction:processed:";

    @Test
    void transactionListener_Success() throws JsonProcessingException {
        String message = """
                 {
                     "idTransacao": "123"
                 }
                """;
        TransactionDTO transaction = new TransactionDTO();
        transaction.setIdTransacao("123");

        when(mapper.readValue(message, TransactionDTO.class)).thenReturn(transaction);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(eq(PROCESSED_KEY_PREFIX + "123"), any(), any()))
                .thenReturn(true);

        consumer.transactionListener(message);

        verify(transactionService, times(1)).analyzeNewTransaction(transaction);
    }

    @Test
    void transactionListener_AlreadyProcessed() throws JsonProcessingException {
        String message = """
                 {
                     "idTransacao": "123"
                 }
                """;
        TransactionDTO transaction = new TransactionDTO();
        transaction.setIdTransacao("123");

        when(mapper.readValue(message, TransactionDTO.class)).thenReturn(transaction);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(eq(PROCESSED_KEY_PREFIX + "123"), any(), any()))
                .thenReturn(false);

        consumer.transactionListener(message);

        verify(transactionService, never()).analyzeNewTransaction(any());
    }

    @Test
    void transactionListener_InvalidJson_ThrowsException() throws JsonProcessingException {
        String message = "json-invalido";

        when(mapper.readValue(message, TransactionDTO.class))
                .thenThrow(new JsonProcessingException("Erro mockado") {});

        PayloadConvertException exception = assertThrows(PayloadConvertException.class, () -> {
            consumer.transactionListener(message);
        });

        assertTrue(exception.getMessage().contains("Erro ao converter mensagem para objeto"));
        verify(transactionService, never()).analyzeNewTransaction(any());
    }

}