package br.com.matera.antifraudservice.entity;


import br.com.matera.antifraudservice.dto.TransactionDTO;
import br.com.matera.antifraudservice.enums.FraudReason;
import br.com.matera.antifraudservice.enums.FraudStatus;
import br.com.matera.antifraudservice.enums.TransactionType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
public class TransactionEntity {

    @Id
    private String idTransacao;

    private String idConta;

    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    private TransactionType tipo;

    private LocalDateTime dataHoraTransacao;

    @Enumerated(EnumType.STRING)
    private FraudStatus status;

    @Enumerated(EnumType.STRING)
    private FraudReason reason;

    private boolean manualAnalysis;

    private LocalDateTime dataHoraPersistencia;


    public static TransactionEntity transactionEntityFromDTO(TransactionDTO dto){
        TransactionEntity entity = new TransactionEntity();
        entity.setIdTransacao(dto.getIdTransacao());
        entity.setIdConta(dto.getIdConta());
        entity.setValor(dto.getValor());
        entity.setTipo(dto.getTipo());
        entity.setDataHoraTransacao(dto.getDataHora());
        return entity;
    }

    @PrePersist
    public void prePersist(){
        this.dataHoraPersistencia = LocalDateTime.now();
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

    public LocalDateTime getDataHoraTransacao() {
        return dataHoraTransacao;
    }

    public void setDataHoraTransacao(LocalDateTime dataHoraTransacao) {
        this.dataHoraTransacao = dataHoraTransacao;
    }

    public FraudStatus getStatus() {
        return status;
    }

    public void setStatus(FraudStatus status) {
        this.status = status;
    }

    public FraudReason getReason() {
        return reason;
    }

    public void setReason(FraudReason reason) {
        this.reason = reason;
    }

    public boolean isManualAnalysis() {
        return manualAnalysis;
    }

    public void setManualAnalysis(boolean manualAnalysis) {
        this.manualAnalysis = manualAnalysis;
    }

    public LocalDateTime getDataHoraPersistencia() {
        return dataHoraPersistencia;
    }

    public void setDataHoraPersistencia(LocalDateTime dataHoraPersistencia) {
        this.dataHoraPersistencia = dataHoraPersistencia;
    }
}
