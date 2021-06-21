package com.example.payment.api;

import com.example.payment.model.PaymentDto;
import com.example.payment.model.PaymentResponse;
import com.example.payment.util.PaymentUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaymentControllerTest {
 
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String uri;

    @BeforeEach
    public void setUp() {
        uri = UriComponentsBuilder.fromUriString("http://localhost").port(port).path("/api/v1/transaction")
                .build().toUriString();
    }

    @Test
    void givenValidRequest_whenSaveTransaction_then200ReturnWithApprove() {
        final var paymentResponseResponseEntity = restTemplate.postForEntity(uri, PaymentUtils.getTestRequest(), PaymentResponse.class);

        Assertions.assertEquals(HttpStatus.OK, paymentResponseResponseEntity.getStatusCode());
        Assertions.assertTrue(paymentResponseResponseEntity.getBody().isApproved());
    }


    @Test
    void givenUnValidRequest_whenSaveTransaction_then400Returned() {
        final var testRequest = PaymentUtils.getTestRequest();
        testRequest.setAmount("-100");
        final var paymentResponseResponseEntity = restTemplate.postForEntity(uri, testRequest, PaymentResponse.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, paymentResponseResponseEntity.getStatusCode());
        Assertions.assertFalse(paymentResponseResponseEntity.getBody().isApproved());
        Assertions.assertTrue(paymentResponseResponseEntity.getBody().getErrorPayment().containsKey("amount"));
    }


    @Test
    void givenValidRequest_whenSaveAndThanGetTransaction_then200ReturnWithSanitizedTransaction() {
        final var testRequest = PaymentUtils.getTestRequest();
        restTemplate.postForEntity(uri, testRequest, PaymentResponse.class);
        final var paymentDtoResponseEntity = restTemplate.getForEntity(uri + "?invoice=" + testRequest.getInvoice(), PaymentDto.class);

        Assertions.assertEquals(HttpStatus.OK, paymentDtoResponseEntity.getStatusCode());
        Assertions.assertEquals(testRequest.getAmount(), paymentDtoResponseEntity.getBody().getAmount());
        Assertions.assertEquals(testRequest.getInvoice(), paymentDtoResponseEntity.getBody().getInvoice());
        Assertions.assertEquals("****", paymentDtoResponseEntity.getBody().getCard().getExpiry());
        Assertions.assertEquals("************4444", paymentDtoResponseEntity.getBody().getCard().getPan());
        Assertions.assertNull(paymentDtoResponseEntity.getBody().getCard().getCvv());
        Assertions.assertEquals(testRequest.getCardholder().getEmail(), paymentDtoResponseEntity.getBody().getCardholder().getEmail());
    }

    @Test
    void givenNotExistedInvoice_whenGetTransactionByInvoice_then204Returned() {
        final var paymentDtoResponseEntity = restTemplate.getForEntity(uri + "?invoice=" + "not-existed-invoice", PaymentDto.class);
        Assertions.assertEquals(HttpStatus.NO_CONTENT, paymentDtoResponseEntity.getStatusCode());
    }
}