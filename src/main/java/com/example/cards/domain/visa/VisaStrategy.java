package com.example.cards.domain.visa;

import com.example.cards.domain.CreditCardBrandStrategy;

public class VisaStrategy implements CreditCardBrandStrategy {

    @Override
    public void validate(String number) {
        String n = normalize(number);

        if (!n.startsWith("4")) {
            throw new IllegalArgumentException("Visa: número deve iniciar com 4");
        }

        if (n.length() != 16) {
            throw new IllegalArgumentException("Visa: tamanho inválido (esperado 16 dígitos)");
        }
    // aqui você pode adicionar Luhn se desejar
    }

    @Override
    public String normalize(String number) {
        return number == null ? "" : number.replaceAll("\\s|-", "");
    }


    @Override
    public double calculateFee(double amount) {
        return amount * 0.018; // 1.8%
    }
}
