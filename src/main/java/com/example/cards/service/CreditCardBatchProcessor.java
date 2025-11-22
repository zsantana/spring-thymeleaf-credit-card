package com.example.cards.service;

import com.example.cards.domain.CreditCard;
import com.example.cards.domain.CreditCardBrand;
import com.example.cards.domain.KafkaTopicStrategyProvider;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

@Service
public class CreditCardBatchProcessor {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CreditCardBatchProcessor.class);
    
    private final KafkaTemplate<String, CreditCard> kafkaTemplate;
    private final KafkaTopicStrategyProvider topicStrategyProvider;
    private static final int BATCH_SIZE = 1000;

    public CreditCardBatchProcessor(KafkaTemplate<String, CreditCard> kafkaTemplate,
                                    KafkaTopicStrategyProvider topicStrategyProvider) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicStrategyProvider = topicStrategyProvider;
    }


    public void processBatchForBrand(CreditCardBrand brand, Queue<CreditCard> brandBuffer) {
        if (brandBuffer == null || brandBuffer.isEmpty()) {
            return;
        }

        List<CreditCard> lote = new ArrayList<>();
        // Drena até BATCH_SIZE itens da fila específica da bandeira
        for (int i = 0; i < BATCH_SIZE; i++) {
            CreditCard card = brandBuffer.poll();
            if (card == null) break;
            lote.add(card);
        }

        if (lote.isEmpty()) return;

        try {
            String topic = topicStrategyProvider.getTopicName(brand);
            log.info("### Processando lote de {} cartões da bandeira {} para o tópico {}", 
                     lote.size(), brand, topic);

            lote.forEach(b -> kafkaTemplate.send(topic, b));

        } catch (Exception e) {
            log.error("Erro ao processar lote da bandeira {}", brand, e);
            // Lógica de retry ou DLQ manual necessária aqui
        }
    }
}
