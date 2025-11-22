package com.example.cards.domain.kafka;

import com.example.cards.domain.CreditCardBrand;
import com.example.cards.domain.KafkaTopicStrategy;
import org.springframework.stereotype.Component;

@Component
public class VisaKafkaTopicStrategy implements KafkaTopicStrategy {
    
    @Override
    public String getTopicName() {
        return "cartoes-visa";
    }
    
    @Override
    public CreditCardBrand getBrand() {
        return CreditCardBrand.VISA;
    }
}
