package com.example.cards.domain.kafka;

import com.example.cards.domain.CreditCardBrand;
import com.example.cards.domain.KafkaTopicStrategy;
import org.springframework.stereotype.Component;

@Component
public class AmexKafkaTopicStrategy implements KafkaTopicStrategy {
    
    @Override
    public String getTopicName() {
        return "cartoes-amex";
    }
    
    @Override
    public CreditCardBrand getBrand() {
        return CreditCardBrand.AMEX;
    }
}
