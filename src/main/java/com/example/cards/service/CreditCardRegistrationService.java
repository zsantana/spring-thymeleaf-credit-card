package com.example.cards.service;

import com.example.cards.domain.CreditCard;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


@Service
public class CreditCardRegistrationService {

    // armazenamento simples em memória (thread-safe)
    private final List<CreditCard> cards = new CopyOnWriteArrayList<>();

    public void register(CreditCard card) {
        // aqui poderia ter persistência, eventos, etc.
        cards.add(card);
    }

    public List<CreditCard> getAllCards() {
        return Collections.unmodifiableList(cards);
    }
}