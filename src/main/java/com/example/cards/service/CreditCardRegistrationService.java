package com.example.cards.service;

import com.example.cards.domain.CreditCard;
import com.example.cards.domain.CreditCardBrand;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


@Service
public class CreditCardRegistrationService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CreditCardRegistrationService.class);

    private final CreditCardBatchProcessor batchProcessor;
    
    // Buffers separados por tipo de cartão
    private final Map<CreditCardBrand, Queue<CreditCard>> buffersByBrand = new ConcurrentHashMap<>();
    private static final int BATCH_SIZE = 1000;
    
    // ExecutorService para processamento paralelo
    //private final ExecutorService executorService;

    public CreditCardRegistrationService(CreditCardBatchProcessor batchProcessor) {
        this.batchProcessor = batchProcessor;
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
                batchProcessor.processBatchForBrand(brand, brandBuffer);
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
            Queue<CreditCard> brandBuffer = buffersByBrand.get(brand);
            batchProcessor.processBatchForBrand(brand, brandBuffer);
        }
    }
}