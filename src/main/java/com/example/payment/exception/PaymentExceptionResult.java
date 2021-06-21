package com.example.payment.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentExceptionResult {
    NULLABLE_VALUE("Value %s should not be nullable."),

    INVALID_VALUE("Invalid value provided."),
    INVOICE_ALREADY_EXIST("Invoice already exists."),
    CARD_EXPIRED("Payment card is expired."),
    INVALID_CARD_DATE_FORMAT("Payment card is expired."),
    INVALID_AMOUNT("Amount value should numeric and greater than 0."),
    INVALID_EMAIL("Invalid cardholder email format."),
    INVALID_PAN("Invalid pan number on credit card.");


    private final String message;
}
