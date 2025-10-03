package br.com.matera.antifraudservice.enums;

public enum FraudReason {
    EXCEEDED_TRANSACTION_LIMIT("Limite excedido para tipo de transação."),
    ACCOUNT_INACTIVE("Conta inativa."),
    SUSPECT_TRANSACTION_INTERVAL_WITHOUT_REVIEW("Existe ao menos uma transação com suspeita de fraude sem análise manual."),
    SUSPECT_TRANSACTION_INTERVAL("Existe ao menos uma transação com suspeita de fraude sem análise manual ativo.");


    private String description;

    FraudReason(String reason) {
        this.description = reason;
    }

    public String getDescription() {
        return description;
    }
}
