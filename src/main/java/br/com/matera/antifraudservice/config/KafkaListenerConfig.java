package br.com.matera.antifraudservice.config;

import br.com.matera.antifraudservice.exceptions.PayloadConvertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaListenerConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaListenerConfig.class);

    @Bean
    public DefaultErrorHandler errorHandler() {

        FixedBackOff fixedBackOff = new FixedBackOff(200L, 2L);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler((record, exception) -> {
            log.warn("Erro encontrado durante a execução do listener");
            log.error("Mensagem descartada: " + record.value() +
                    " devido a: " + exception.getMessage());
        }, fixedBackOff);

        errorHandler.addNotRetryableExceptions(PayloadConvertException.class);

        return errorHandler;
    }


}
