package com.example.payment.validator;

import com.example.payment.exception.PaymentExceptionResult;
import com.example.payment.model.PaymentDto;
import com.example.payment.util.PaymentUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class PaymentRequestValidatorTest {

    @Test
    void givenValidRequest_whenValidationIsCalled_thenErrorMapIsEmpty() {
        PaymentDto testRequest = PaymentUtils.getTestRequest();
        Map<String, String> stringStringMap = PaymentRequestValidator.validatePaymentRequest(testRequest);

        Assertions.assertTrue(stringStringMap.isEmpty());
    }

    @Test
    void givenInValidRequest_whenValidationIsCalled_thenErrorMapIsNotEmpty() {
        PaymentDto testRequest = PaymentUtils.getTestRequest();
        testRequest.getCard().setPan("4200000000000001");
        Map<String, String> stringStringMap = PaymentRequestValidator.validatePaymentRequest(testRequest);

        Assertions.assertEquals(1, stringStringMap.size());
        Assertions.assertEquals(PaymentExceptionResult.INVALID_PAN.getMessage(), stringStringMap.get("pan"));
    }

    @Test
    void givenRequestWithNullableAmount_whenValidationIsCalled_thenErrorMapIsPopulated() {
        PaymentDto testRequest = PaymentUtils.getTestRequest();
        testRequest.getCard().setPan(null);
        Map<String, String> stringStringMap = PaymentRequestValidator.validatePaymentRequest(testRequest);

        Assertions.assertEquals(1, stringStringMap.size());
        Assertions.assertEquals(String.format(PaymentExceptionResult.NULLABLE_VALUE.getMessage(), "pan"), stringStringMap.get("pan"));
    }

    @Test
    void givenTransactionWithZeroAmount_whenValidationIsCalled_thenErrorMapIsPopulated() {
        Map<String, String> errorMap = new HashMap<>();
        PaymentRequestValidator.validateAmount("0", errorMap);

        Assertions.assertEquals(1, errorMap.size());
        Assertions.assertEquals(PaymentExceptionResult.INVALID_AMOUNT.getMessage(), errorMap.get("amount"));
    }

    @Test
    void givenTransactionWithAmountAsChar_whenValidationIsCalled_thenErrorMapIsPopulated() {
        Map<String, String> errorMap = new HashMap<>();
        PaymentRequestValidator.validateAmount("sas", errorMap);

        Assertions.assertEquals(1, errorMap.size());
        Assertions.assertEquals(PaymentExceptionResult.INVALID_AMOUNT.getMessage(), errorMap.get("amount"));
    }

    @Test
    void givenTransactionInvalidWithEmail_whenValidationIsCalled_thenErrorMapIsPopulated() {
        Map<String, String> errorMap = new HashMap<>();
        PaymentDto.CardHolder cardholder = PaymentUtils.getTestRequest().getCardholder();
        cardholder.setEmail("invalid-email-format");
        PaymentRequestValidator.validateEmail(Optional.of(cardholder), errorMap);

        Assertions.assertEquals(1, errorMap.size());
        Assertions.assertEquals(PaymentExceptionResult.INVALID_EMAIL.getMessage(), errorMap.get("email"));
    }

    @Test
    void givenTransactionWithInvalidCardDate_whenValidationIsCalled_thenErrorMapIsPopulated() {
        Map<String, String> errorMap = new HashMap<>();
        PaymentDto.Card card = PaymentUtils.getTestRequest().getCard();
        card.setExpiry("fwrew");
        PaymentRequestValidator.validateExpireDate(Optional.of(card), errorMap);

        Assertions.assertEquals(1, errorMap.size());
        Assertions.assertEquals(PaymentExceptionResult.INVALID_CARD_DATE_FORMAT.getMessage(), errorMap.get("expiry"));
    }

    @Test
    void givenTransactionWithExpiredCardDate_whenValidationIsCalled_thenErrorMapIsPopulated() {
        Map<String, String> errorMap = new HashMap<>();
        PaymentDto.Card card = PaymentUtils.getTestRequest().getCard();
        card.setExpiry("0404");
        PaymentRequestValidator.validateExpireDate(Optional.of(card), errorMap);

        Assertions.assertEquals(1, errorMap.size());
        Assertions.assertEquals(PaymentExceptionResult.INVALID_CARD_DATE_FORMAT.getMessage(), errorMap.get("expiry"));
    }
    

    @Test
    void givenTransactionWithInvalidLuhnCheck_whenValidationIsCalled_thenErrorMaoIsPopulated() {
        Map<String, String> errorMap = new HashMap<>();
        PaymentDto.Card card = PaymentUtils.getTestRequest().getCard();
        card.setPan("4200000000000001");

        PaymentRequestValidator.validatePan(Optional.of(card), errorMap);

        Assertions.assertEquals(1, errorMap.size());
        Assertions.assertEquals(PaymentExceptionResult.INVALID_PAN.getMessage(), errorMap.get("pan"));
    }


    @Test
    void givenTransactionWithValidLuhnCheck_whenValidationIsCalled_thenErrorMapEmpty() {
        Map<String, String> errorMap = new HashMap<>();
        PaymentDto.Card card = PaymentUtils.getTestRequest().getCard();

        PaymentRequestValidator.validatePan(Optional.of(card), errorMap);

        Assertions.assertEquals(0, errorMap.size());
    }

}