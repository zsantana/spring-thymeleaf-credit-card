package com.example.cards.service;

import com.example.cards.domain.CreditCard;
import com.example.cards.domain.CreditCardBrand;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;


@Service
public class CreditCardRegistrationService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CreditCardRegistrationService.class);

    private final KafkaTemplate<String, CreditCard> kafkaTemplate;
    
    // Tópicos separados por bandeira
    private static final String TOPIC_VISA = "cartoes-visa";
    private static final String TOPIC_MASTERCARD = "cartoes-mastercard";
    private static final String TOPIC_AMEX = "cartoes-amex";

    // Buffers separados por tipo de cartão
    private final Map<CreditCardBrand, Queue<CreditCard>> buffersByBrand = new ConcurrentHashMap<>();
    private static final int BATCH_SIZE = 1000;

    public CreditCardRegistrationService(KafkaTemplate<String, CreditCard> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        // Inicializa os buffers para cada bandeira
        for (CreditCardBrand brand : CreditCardBrand.values()) {
            buffersByBrand.put(brand, new ConcurrentLinkedQueue<>());
        }
    }

    public void register(CreditCard card) {
        CreditCardBrand brand = card.getBrand();
        Queue<CreditCard> brandBuffer = buffersByBrand.get(brand);
        
        if (brandBuffer != null) {
            brandBuffer.offer(card);
            
            // Processa o lote se atingir o tamanho configurado
            if (brandBuffer.size() >= BATCH_SIZE) {
                processBatchForBrand(brand);
            }
        }

    }

    public List<CreditCard> getAllCards() {
        List<CreditCard> allCards = new ArrayList<>();
        for (Queue<CreditCard> buffer : buffersByBrand.values()) {
            allCards.addAll(buffer);
        }
        return Collections.unmodifiableList(allCards);
    }

    @Scheduled(fixedDelay = 500) 
    public synchronized void processBatch() {
        // Processa os lotes de cada bandeira
        for (CreditCardBrand brand : CreditCardBrand.values()) {
            processBatchForBrand(brand);
        }
    }

    private void processBatchForBrand(CreditCardBrand brand) {
        Queue<CreditCard> brandBuffer = buffersByBrand.get(brand);
        
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
            String topic = getTopicForBrand(brand);
            log.info("### Processando lote de {} cartões da bandeira {} para o tópico {}", 
                     lote.size(), brand, topic);
            
            // Envia para o tópico específico da bandeira
            lote.forEach(card -> kafkaTemplate.send(topic, card));

        } catch (Exception e) {
            log.error("Erro ao processar lote da bandeira {}", brand, e);
            // Lógica de retry ou DLQ manual necessária aqui
        }
    }

    private String getTopicForBrand(CreditCardBrand brand) {
        switch (brand) {
            case VISA:
                return TOPIC_VISA;
            case MASTERCARD:
                return TOPIC_MASTERCARD;
            case AMEX:
                return TOPIC_AMEX;
            default:
                return "cartoes-outros";
        }
    }
}