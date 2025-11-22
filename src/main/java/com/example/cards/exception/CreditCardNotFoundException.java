package com.example.cards.exception;

/**
 * Exceção lançada quando um cartão de crédito não é encontrado
 */
public class CreditCardNotFoundException extends RuntimeException {

    public CreditCardNotFoundException(String message) {
        super(message);
    }

    public CreditCardNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CreditCardNotFoundException(Long cardId) {
        super("Cartão de crédito com ID " + cardId + " não foi encontrado");
    }

    public CreditCardNotFoundException(String cardNumber, String holderName) {
        super("Cartão com número " + cardNumber + " do titular " + holderName + " não foi encontrado");
    }
}
