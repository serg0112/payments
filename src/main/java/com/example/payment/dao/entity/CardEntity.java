package com.example.payment.dao.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(exclude = {"uuid", "cvv"})
public class CardEntity implements Serializable {
    private UUID uuid;
    private String pan;
    private String expiry;
    private String lastFourDigits;
    private transient String cvv;
    // 1 card holder many cards
    private CardHolderEntity cardHolderEntity;
}
