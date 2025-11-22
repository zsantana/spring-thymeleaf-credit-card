package com.example.cards.domain;

public class DefaultCreditCard implements CreditCard {

    private final String holderName;
    private final String number;
    private final CreditCardBrand brand;

    public DefaultCreditCard(String holderName, String number, CreditCardBrand brand) {
        
        if (holderName == null || holderName.isBlank()) {
            throw new IllegalArgumentException("Nome do titular é obrigatório");
        }
        
        this.holderName = holderName.trim();
        this.brand = brand;

        CreditCardBrandStrategy strategy = CreditCardBrandFactory.getStrategy(brand);
        strategy.validate(number);
        this.number = strategy.normalize(number);
    }


    @Override
    public String getHolderName() {
        return holderName;
    }


    @Override
    public String getNumber() {
        return number;
    }


    @Override
    public CreditCardBrand getBrand() {
        return brand;
    }
}
