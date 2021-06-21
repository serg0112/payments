package com.example.payment.services.impl;

import com.example.payment.dao.entity.CardHolderEntity;
import com.example.payment.services.BaseSanitizer;
import org.springframework.stereotype.Service;

@Service
public class CardHolderSanitizerServiceImpl implements BaseSanitizer<CardHolderEntity> {

    @Override
    public CardHolderEntity sanitizeEntity(CardHolderEntity cardHolderEntity) {
        cardHolderEntity.setName(encode(cardHolderEntity.getName()));
        return cardHolderEntity;
    }

    @Override
    public CardHolderEntity deSanitizeEntity(CardHolderEntity cardHolderEntity) {
        cardHolderEntity.setName(decode(cardHolderEntity.getName()));
        return cardHolderEntity;
    }
}
