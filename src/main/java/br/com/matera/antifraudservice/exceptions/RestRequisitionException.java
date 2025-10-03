package br.com.matera.antifraudservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Erro na requisição de serviços externos.")
public class RestRequisitionException extends GenericException {

    public RestRequisitionException() {
    }

    public RestRequisitionException(String message) {
        super(message);
    }

    public RestRequisitionException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestRequisitionException(Throwable cause) {
        super(cause);
    }
}
