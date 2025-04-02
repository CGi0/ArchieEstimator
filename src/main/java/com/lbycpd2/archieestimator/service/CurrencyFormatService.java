package com.lbycpd2.archieestimator.service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;

public class CurrencyFormatService {

    private static CurrencyFormatService instance;
    private final NumberFormat currencyFormat;

    private CurrencyFormatService() {
        // Default to the Philippine Peso
        currencyFormat = NumberFormat.getCurrencyInstance();
        currencyFormat.setCurrency(Currency.getInstance("PHP"));
    }

    public static synchronized CurrencyFormatService getInstance() {
        if (instance == null) {
            instance = new CurrencyFormatService();
        }
        return instance;
    }

    public String format(BigDecimal amount) {
        return currencyFormat.format(amount);
    }

    public NumberFormat getCurrenyFormat(){
        return currencyFormat;
    }
}
