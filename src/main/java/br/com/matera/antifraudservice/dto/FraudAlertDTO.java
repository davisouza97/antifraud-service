package br.com.matera.antifraudservice.dto;

import br.com.matera.antifraudservice.enums.FraudReason;
import br.com.matera.antifraudservice.enums.FraudStatus;

import java.time.LocalDateTime;

public class FraudAlertDTO {

    private String idTransacao;
    private String idConta;
    private FraudReason motivo;
    private FraudStatus status;
    private LocalDateTime dataHoraAnalise;

    public String getIdTransacao() {
        return idTransacao;
    }

    public void setIdTransacao(String idTransacao) {
        this.idTransacao = idTransacao;
    }

    public String getIdConta() {
        return idConta;
    }

    public void setIdConta(String idConta) {
        this.idConta = idConta;
    }

    public FraudReason getMotivo() {
        return motivo;
    }

    public void setMotivo(FraudReason motivo) {
        this.motivo = motivo;
    }

    public FraudStatus getStatus() {
        return status;
    }

    public void setStatus(FraudStatus status) {
        this.status = status;
    }

    public LocalDateTime getDataHoraAnalise() {
        return dataHoraAnalise;
    }

    public void setDataHoraAnalise(LocalDateTime dataHoraAnalise) {
        this.dataHoraAnalise = dataHoraAnalise;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final FraudAlertDTO instance = new FraudAlertDTO();

        public Builder idTransacao(String idTransacao) {
            instance.idTransacao = idTransacao;
            return this;
        }

        public Builder idConta(String idConta) {
            instance.idConta = idConta;
            return this;
        }

        public Builder motivo(FraudReason motivo) {
            instance.motivo = motivo;
            return this;
        }

        public Builder status(FraudStatus status) {
            instance.status = status;
            return this;
        }

        public Builder dataHoraAnalise(LocalDateTime dataHoraAnalise) {
            instance.dataHoraAnalise = dataHoraAnalise;
            return this;
        }

        public FraudAlertDTO build() {
            return instance;
        }
    }
}
