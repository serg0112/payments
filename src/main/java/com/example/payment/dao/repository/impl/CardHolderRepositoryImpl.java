package com.example.payment.dao.repository.impl;

import com.example.payment.dao.entity.CardHolderEntity;
import com.example.payment.dao.repository.CardHolderRepository;
import com.example.payment.services.BaseSanitizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class CardHolderRepositoryImpl implements CardHolderRepository {

    private final BaseSanitizer<CardHolderEntity> sanitizerService;
    private final Set<CardHolderEntity> cardHolderDataStore = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Override
    public CardHolderEntity saveCardHolder(final CardHolderEntity cardHolderEntity) {
        cardHolderEntity.setUuid(UUID.randomUUID());
        cardHolderDataStore.add(sanitizerService.sanitizeEntity(cardHolderEntity));
        return cardHolderEntity;
    }

    // assuming email is unique per client
    @Override
    public CardHolderEntity findCardHolder(final CardHolderEntity cardHolderEntity) {
        return cardHolderDataStore.stream()
                .filter(ch -> ch.getEmail().equals(cardHolderEntity.getEmail()))
                .findAny()
                .orElse(null);
    }
}
