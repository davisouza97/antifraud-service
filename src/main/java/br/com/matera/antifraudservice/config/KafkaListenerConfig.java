package br.com.matera.antifraudservice.config;

import br.com.matera.antifraudservice.exceptions.PayloadConvertException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaListenerConfig {


    @Bean
    public DefaultErrorHandler errorHandler() {

        FixedBackOff fixedBackOff = new FixedBackOff(200L, 2L);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler((record, exception) -> {
            //TODO LOG
            System.out.println("Mensagem descartada: " + record.value() +
                    " devido a: " + exception.getMessage());
        }, fixedBackOff);

        errorHandler.addNotRetryableExceptions(PayloadConvertException.class);

        return errorHandler;
    }


}
