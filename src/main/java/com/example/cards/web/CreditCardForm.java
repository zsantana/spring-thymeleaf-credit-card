package com.example.cards.web;

import com.example.cards.domain.CreditCardBrand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public class CreditCardForm {

    @NotBlank(message = "Nome do titular é obrigatório")
    private String holderName;


    @NotBlank(message = "Número é obrigatório")
    private String number;


    @NotNull(message = "Bandeira é obrigatória")
    private CreditCardBrand brand;


    public String getHolderName() {
        return holderName;
    }


    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }


    public String getNumber() {
        return number;
    }


    public void setNumber(String number) {
        this.number = number;
    }


    public CreditCardBrand getBrand() {
        return brand;
    }


    public void setBrand(CreditCardBrand brand) {
        this.brand = brand;
    }
}