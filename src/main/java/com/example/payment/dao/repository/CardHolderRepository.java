package com.example.payment.dao.repository;

import com.example.payment.dao.entity.CardHolderEntity;

public interface CardHolderRepository {

    CardHolderEntity saveCardHolder(CardHolderEntity cardHolderEntity);

    CardHolderEntity findCardHolder(CardHolderEntity cardHolderEntity);
}
