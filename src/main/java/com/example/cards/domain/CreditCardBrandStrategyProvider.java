package com.example.cards.domain;

import com.example.cards.domain.amex.AmexStrategy;
import com.example.cards.domain.master.MasterCardStrategy;
import com.example.cards.domain.visa.VisaStrategy;

public enum CreditCardBrandStrategyProvider {
    
    VISA(new VisaStrategy()),
    MASTERCARD(new MasterCardStrategy()),
    AMEX(new AmexStrategy());

    private final CreditCardBrandStrategy strategy;

    CreditCardBrandStrategyProvider(CreditCardBrandStrategy strategy) {
        this.strategy = strategy;
    }

    public CreditCardBrandStrategy get() {
        return strategy;
    }

    public static CreditCardBrandStrategy getByBrand(CreditCardBrand brand) {
        return valueOf(brand.name()).get();
    }
}
