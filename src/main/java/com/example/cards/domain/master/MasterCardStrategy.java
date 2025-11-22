package com.example.cards.domain.master;

import com.example.cards.domain.CreditCardBrandStrategy;

public class MasterCardStrategy implements CreditCardBrandStrategy {

    @Override
    public void validate(String number) {
        String n = normalize(number);

        if (!n.startsWith("5")) {
            throw new IllegalArgumentException("MasterCard: número deve iniciar com 5");
        }

        if (n.length() != 16) {
            throw new IllegalArgumentException("MasterCard: tamanho inválido (esperado 16 dígitos)");
        }
    }

    @Override
    public String normalize(String number) {
        return number == null ? "" : number.replaceAll("\\s|-", "");
    }

    @Override
    public double calculateFee(double amount) {
        return amount * 0.015; // 1.5%
    }
}
