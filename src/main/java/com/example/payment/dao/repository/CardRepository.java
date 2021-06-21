package com.example.payment.dao.repository;

import com.example.payment.dao.entity.CardEntity;

public interface CardRepository {

    CardEntity saveCard(CardEntity cardEntityEntity);

    CardEntity findCardByPan(String pan);
}
