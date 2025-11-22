package com.example.cards.domain;

public interface KafkaTopicStrategy {
    String getTopicName();
    CreditCardBrand getBrand();
}
