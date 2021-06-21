package com.example.payment.model;

import lombok.Data;

@Data
public class ErrorPayment {
    private String currency;
    private String email;
    private String expiry;
}
