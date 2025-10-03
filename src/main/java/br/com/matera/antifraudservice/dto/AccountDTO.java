package br.com.matera.antifraudservice.dto;

import java.math.BigDecimal;

public class AccountDTO {
    private String idConta;

    private BigDecimal valorLimiteParaDebito;

    private BigDecimal valorLimiteParaCredito;

    private boolean contaAtiva;

    public String getIdConta() {
        return idConta;
    }

    public void setIdConta(String idConta) {
        this.idConta = idConta;
    }

    public BigDecimal getValorLimiteParaDebito() {
        return valorLimiteParaDebito;
    }

    public void setValorLimiteParaDebito(BigDecimal valorLimiteParaDebito) {
        this.valorLimiteParaDebito = valorLimiteParaDebito;
    }

    public BigDecimal getValorLimiteParaCredito() {
        return valorLimiteParaCredito;
    }

    public void setValorLimiteParaCredito(BigDecimal valorLimiteParaCredito) {
        this.valorLimiteParaCredito = valorLimiteParaCredito;
    }

    public boolean isContaAtiva() {
        return contaAtiva;
    }

    public void setContaAtiva(boolean contaAtiva) {
        this.contaAtiva = contaAtiva;
    }
}
