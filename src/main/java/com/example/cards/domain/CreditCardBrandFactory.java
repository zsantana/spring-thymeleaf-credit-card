package com.example.cards.domain;

import java.util.HashMap;
import java.util.Map;

import com.example.cards.domain.amex.AmexStrategy;
import com.example.cards.domain.master.MasterCardStrategy;
import com.example.cards.domain.visa.VisaStrategy;

public class CreditCardBrandFactory {

    private static final Map<CreditCardBrand, CreditCardBrandStrategy> strategies = new HashMap<>();
    
    static {
        strategies.put(CreditCardBrand.VISA, new VisaStrategy());
        strategies.put(CreditCardBrand.MASTERCARD, new MasterCardStrategy());
        strategies.put(CreditCardBrand.AMEX, new AmexStrategy());
    }
    
    public static CreditCardBrandStrategy getStrategy(CreditCardBrand brand) {
        CreditCardBrandStrategy strategy = strategies.get(brand);
        if (strategy == null) {
            throw new IllegalArgumentException("Bandeira de cartão desconhecida");
        }
        return strategy;
    }
}
