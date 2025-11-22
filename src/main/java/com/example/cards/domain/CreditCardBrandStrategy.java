package com.example.cards.domain;

public interface CreditCardBrandStrategy {
    /**
    * Valida o número do cartão conforme regras da bandeira.
    * Deve lançar IllegalArgumentException se inválido.
    */
    void validate(String number);


    /** Normaliza o número (remover espaços, traços, etc) */
    String normalize(String number);


    /** Exemplo de cálculo de tarifa (opcional) */
    double calculateFee(double amount);
}