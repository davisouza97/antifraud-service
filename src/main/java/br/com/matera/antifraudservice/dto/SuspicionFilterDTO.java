package br.com.matera.antifraudservice.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class SuspicionFilterDTO {

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataInicial;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataFinal;

    private String idConta;

    public SuspicionFilterDTO() {
    }

    public SuspicionFilterDTO(LocalDate dataInicial, LocalDate dataFinal, BigDecimal valorMinimo, BigDecimal valorMaximo, String idConta) {
        this.dataInicial = dataInicial;
        this.dataFinal = dataFinal;
        this.idConta = idConta;
    }

    public LocalDate getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(LocalDate dataInicial) {
        this.dataInicial = dataInicial;
    }

    public LocalDate getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(LocalDate dataFinal) {
        this.dataFinal = dataFinal;
    }

    public String getIdConta() {
        return idConta;
    }

    public void setIdConta(String idConta) {
        this.idConta = idConta;
    }

    public LocalDateTime getDataInicialDateTime() {
        return dataInicial != null ? dataInicial.atStartOfDay() : null;
    }

    public LocalDateTime getDataFinalDateTime() {
        return dataFinal != null ? dataFinal.atTime(LocalTime.MAX) : null;
    }

}
