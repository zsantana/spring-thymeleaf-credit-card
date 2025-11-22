package com.example.cards.domain;


public interface CreditCard {
    String getHolderName();
    String getNumber();
    CreditCardBrand getBrand();
}