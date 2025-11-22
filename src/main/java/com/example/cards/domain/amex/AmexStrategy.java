package com.example.cards.domain.amex;

import com.example.cards.domain.CreditCardBrandStrategy;

public class AmexStrategy implements CreditCardBrandStrategy {

    @Override
    public void validate(String number) {
        String n = normalize(number);

        if (!n.startsWith("3")) {
            throw new IllegalArgumentException("Amex: número deve iniciar com 3");
        }

        if (n.length() != 15) {
            throw new IllegalArgumentException("Amex: tamanho inválido (esperado 15 dígitos)");
        }
    }

    @Override
    public String normalize(String number) {
        return number == null ? "" : number.replaceAll("\\s|-", "");
    }

    @Override
    public double calculateFee(double amount) {
        return amount * 0.02; // 2.0%
    }
}
