package com.example.cards.domain;


public interface CreditCard {
    String getUUID();
    String getHolderName();
    String getNumber();
    CreditCardBrand getBrand();
}