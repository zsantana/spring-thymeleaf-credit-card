package com.example.cards.domain.kafka;

import com.example.cards.domain.CreditCardBrand;
import com.example.cards.domain.KafkaTopicStrategy;
import org.springframework.stereotype.Component;

@Component
public class MastercardKafkaTopicStrategy implements KafkaTopicStrategy {
    
    @Override
    public String getTopicName() {
        return "cartoes-mastercard";
    }
    
    @Override
    public CreditCardBrand getBrand() {
        return CreditCardBrand.MASTERCARD;
    }
}
