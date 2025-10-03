package br.com.matera.antifraudservice.utils;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FeatureFlag {

    @Value("${features.transactionLookbackDays}")
    private int transactionLookbackDays;

    @Value("${features.maxSameTypeTransactions}")
    private int maxSameTypeTransactions;

    @Value("${features.sameTypeTransactionWindowInMinutes}")
    private int SameTypeTransactionWindowInMinutes;

    public int getTransactionLookbackDays() {
        return transactionLookbackDays;
    }

    public int getMaxSameTypeTransactions() {
        return maxSameTypeTransactions;
    }

    public int getSameTypeTransactionWindowInMinutes() {
        return SameTypeTransactionWindowInMinutes;
    }
}
