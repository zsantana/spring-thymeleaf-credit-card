package com.example.cards.service;

import com.example.cards.domain.CreditCard;
import com.example.cards.domain.CreditCardBrand;
import com.example.cards.domain.KafkaTopicStrategyProvider;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CompletableFuture;

import jakarta.annotation.PreDestroy;


@Service
public class CreditCardRegistrationService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CreditCardRegistrationService.class);

    private final KafkaTemplate<String, CreditCard> kafkaTemplate;
    private final KafkaTopicStrategyProvider topicStrategyProvider;
    
    // Buffers separados por tipo de cartão
    private final Map<CreditCardBrand, Queue<CreditCard>> buffersByBrand = new ConcurrentHashMap<>();
    private static final int BATCH_SIZE = 1000;
    
    // ExecutorService para processamento paralelo
    private final ExecutorService executorService;

    public CreditCardRegistrationService(KafkaTemplate<String, CreditCard> kafkaTemplate,
                                         KafkaTopicStrategyProvider topicStrategyProvider) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicStrategyProvider = topicStrategyProvider;
        // Inicializa os buffers para cada bandeira
        for (CreditCardBrand brand : CreditCardBrand.values()) {
            buffersByBrand.put(brand, new ConcurrentLinkedQueue<>());
        }
        // Cria um pool de threads - uma para cada bandeira
        this.executorService = Executors.newFixedThreadPool(CreditCardBrand.values().length);
    }
    
    @PreDestroy
    public void shutdown() {
        log.info("Encerrando ExecutorService...");
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, java.util.concurrent.TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
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
    public void processBatch() {
        // Processa os lotes de cada bandeira em paralelo usando threads
        for (CreditCardBrand brand : CreditCardBrand.values()) {
            executorService.submit(() -> {
                try {
                    processBatchForBrand(brand);
                } catch (Exception e) {
                    log.error("Erro no processamento paralelo da bandeira {}", brand, e);
                }
            });
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
            String topic = topicStrategyProvider.getTopicName(brand);
            log.info("### Processando lote de {} cartões da bandeira {} para o tópico {}", 
                     lote.size(), brand, topic);
            
            // Envia para o tópico específico da bandeira de forma assíncrona
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            
            lote.forEach(card -> {
                CompletableFuture<Void> future = kafkaTemplate.send(topic, card)
                    .thenAccept(result -> {
                        log.debug("Cartão {} enviado com sucesso para o tópico {} na partição {}", 
                                 card.getNumber(), topic, result.getRecordMetadata().partition());
                    })
                    .exceptionally(ex -> {
                        log.error("Erro ao enviar cartão {} para o tópico {}: {}", 
                                 card.getNumber(), topic, ex.getMessage());
                        // Aqui você pode implementar lógica de retry ou enviar para DLQ
                        return null;
                    });
                futures.add(future);
            });
            
            // Aguarda todas as operações assíncronas completarem (opcional)
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(() -> log.info("Lote de {} cartões da bandeira {} processado com sucesso", 
                                       lote.size(), brand))
                .exceptionally(ex -> {
                    log.error("Erro ao processar lote completo da bandeira {}", brand, ex);
                    return null;
                });

        } catch (Exception e) {
            log.error("Erro ao processar lote da bandeira {}", brand, e);
            // Lógica de retry ou DLQ manual necessária aqui
        }
    }
}