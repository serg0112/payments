package com.example.payment.dao.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(exclude = "uuid")
public class CardHolderEntity implements Serializable {
    private UUID uuid;
    private String name;
    private String email;
}
