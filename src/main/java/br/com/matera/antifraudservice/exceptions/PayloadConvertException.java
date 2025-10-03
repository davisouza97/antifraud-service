package br.com.matera.antifraudservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Erro na conversão de payload.")
public class PayloadConvertException extends GenericException{

    public PayloadConvertException() {
    }

    public PayloadConvertException(String message) {
        super(message);
    }

    public PayloadConvertException(String message, Throwable cause) {
        super(message, cause);
    }

    public PayloadConvertException(Throwable cause) {
        super(cause);
    }
}
