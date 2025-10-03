package br.com.matera.antifraudservice.enums;

public enum TransactionType {
    DEBITO("Débito"),
    CREDITO("Crédito");

    private String name;

    TransactionType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
