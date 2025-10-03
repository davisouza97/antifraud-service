package br.com.matera.antifraudservice.dto;

import br.com.matera.antifraudservice.entity.TransactionEntity;
import br.com.matera.antifraudservice.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class TransactionResponseDTO {

    private String idTransacao;

    private String idConta;

    private BigDecimal valor;

    private TransactionType tipo;

    private LocalDateTime dataTransacao;

    private String status;

    private String reason;

    public static TransactionResponseDTO getTransactionResponseDTOFromEntity(TransactionEntity entity) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.idTransacao = entity.getIdTransacao();
        dto.idConta = entity.getIdConta();
        dto.valor = entity.getValor();
        dto.tipo = entity.getTipo();
        dto.dataTransacao = entity.getDataHoraTransacao();
        dto.status = entity.getStatus().name();
        dto.reason = Objects.nonNull(entity.getReason()) ? entity.getReason().getDescription() : entity.getStatus().name();
        return dto;
    }

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

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public TransactionType getTipo() {
        return tipo;
    }

    public void setTipo(TransactionType tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getDataTransacao() {
        return dataTransacao;
    }

    public void setDataTransacao(LocalDateTime dataTransacao) {
        this.dataTransacao = dataTransacao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
