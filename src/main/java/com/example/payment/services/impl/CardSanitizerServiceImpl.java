package com.example.payment.services.impl;

import com.example.payment.dao.entity.CardEntity;
import com.example.payment.services.BaseSanitizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardSanitizerServiceImpl implements BaseSanitizer<CardEntity> {

    private final CardHolderSanitizerServiceImpl cardHolderSanitizerService;

    @Override
    public CardEntity sanitizeEntity(final CardEntity cardEntity) {
        cardEntity.setExpiry(encode(cardEntity.getExpiry()));
        cardEntity.setPan(encode(cardEntity.getPan()));
        cardHolderSanitizerService.sanitizeEntity(cardEntity.getCardHolderEntity());
        return cardEntity;
    }

    @Override
    public CardEntity deSanitizeEntity(final CardEntity cardEntity) {
        cardEntity.setExpiry(decode(cardEntity.getExpiry()));
        cardEntity.setPan(decode(cardEntity.getPan()));
        cardHolderSanitizerService.deSanitizeEntity(cardEntity.getCardHolderEntity());
        return cardEntity;
    }
}
