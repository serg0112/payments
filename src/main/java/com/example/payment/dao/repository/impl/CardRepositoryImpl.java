package com.example.payment.dao.repository.impl;

import com.example.payment.dao.entity.CardEntity;
import com.example.payment.dao.repository.CardRepository;
import com.example.payment.services.BaseSanitizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class CardRepositoryImpl implements CardRepository {

    private final BaseSanitizer<CardEntity> sanitizerService;
    private final Set<CardEntity> cardDataStore = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Override
    public CardEntity saveCard(final CardEntity cardEntity) {
        cardEntity.setUuid(UUID.randomUUID());
        cardDataStore.add(sanitizerService.sanitizeEntity(cardEntity));
        return cardEntity;
    }

    @Override
    public CardEntity findCardByPan(final String pan) {
        return cardDataStore.stream().filter(c -> sanitizerService.decode(c.getPan()).equals(pan)).findAny().orElse(null);
    }
}
