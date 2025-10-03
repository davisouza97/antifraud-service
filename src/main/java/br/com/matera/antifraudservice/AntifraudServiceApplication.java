package br.com.matera.antifraudservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AntifraudServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AntifraudServiceApplication.class, args);
    }

}
