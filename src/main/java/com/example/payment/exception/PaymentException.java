package com.example.payment.exception;


import lombok.Getter;

@Getter
public class PaymentException extends RuntimeException {

    private final String fieldName;
    private final PaymentExceptionResult result;

    public PaymentException(final PaymentExceptionResult result, final String fieldName) {
        super(String.format(result.getMessage(), fieldName));
        this.result = result;
        this.fieldName = fieldName;
    }
}
