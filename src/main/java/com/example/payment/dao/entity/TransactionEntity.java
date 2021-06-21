package com.example.payment.dao.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode(exclude = "uuid")
public class TransactionEntity implements Serializable {
    private UUID uuid;
    private String invoice;
    private String amount;
    private String currency;
    private CardEntity cardEntity;
}
