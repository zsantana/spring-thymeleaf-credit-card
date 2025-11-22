package com.example.cards.domain;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class KafkaTopicStrategyProvider {
    
    private final Map<CreditCardBrand, KafkaTopicStrategy> strategies;
    
    public KafkaTopicStrategyProvider(List<KafkaTopicStrategy> strategyList) {
        this.strategies = strategyList.stream()
            .collect(Collectors.toMap(
                KafkaTopicStrategy::getBrand,
                Function.identity()
            ));
    }
    
    public String getTopicName(CreditCardBrand brand) {
        KafkaTopicStrategy strategy = strategies.get(brand);
        if (strategy != null) {
            return strategy.getTopicName();
        }
        return "cartoes-outros"; // fallback
    }
}
