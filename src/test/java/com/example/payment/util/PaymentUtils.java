package com.example.payment.util;

import com.example.payment.model.PaymentDto;

public class PaymentUtils {

    public static PaymentDto getTestRequest() {
        PaymentDto.CardHolder cardHolder = new PaymentDto.CardHolder();
        cardHolder.setName("test test");
        cardHolder.setEmail("test@gmail.com");

        PaymentDto.Card card = new PaymentDto.Card();
        card.setPan("5555555555554444");
        card.setExpiry("0625");
        card.setCvv("123");

        return PaymentDto.builder()
                .amount("100")
                .invoice("12345")
                .currency("UDS")
                .card(card)
                .cardholder(cardHolder)
                .build();
    }
}
