package com.example.payment.api;

import com.example.payment.exception.PaymentException;
import com.example.payment.mapper.TransactionMapper;
import com.example.payment.model.PaymentDto;
import com.example.payment.model.PaymentResponse;
import com.example.payment.services.PaymentService;
import com.example.payment.validator.PaymentRequestValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

import static java.util.Objects.isNull;


@Slf4j
@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/transaction")
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentDto paymentDto) {
        log.info("started processing invoice {}", paymentDto.getInvoice());
        final Map<String, String> errorMap = PaymentRequestValidator.validatePaymentRequest(paymentDto);
        if (errorMap.isEmpty()) {
            paymentService.saveTransaction(TransactionMapper.mapTransactionToEntity(paymentDto));
            log.info("finished processing invoice {}", paymentDto.getInvoice());
            return ResponseEntity.ok(PaymentResponse.builder().approved(true).build());
        }
        log.debug("finished with exceptions: {}", errorMap);
        return new ResponseEntity<>(PaymentResponse.builder().errorPayment(errorMap).build(), HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/transaction")
    public ResponseEntity<PaymentDto> getPayment(@RequestParam(name = "invoice") String invoice) {
        log.info("started looking for transaction with invoice: {}", invoice);
        final var transactionByInvoice = TransactionMapper.mapTransactionEntityToPaymentDto(paymentService.getTransaction(invoice));
        if (isNull(transactionByInvoice)) {
            log.info("finished looking for transaction. no such invoice: {} found", invoice);
            return ResponseEntity.noContent().build();
        }
        log.info("finished looking for transaction. invoice: {} found", invoice);
        return ResponseEntity.ok(transactionByInvoice);
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<PaymentResponse> handleException(PaymentException e) {
        final Map<String, String> errorMap = Collections.singletonMap(e.getFieldName(), e.getResult().getMessage());
        return new ResponseEntity<>(PaymentResponse.builder().errorPayment(errorMap).build(), HttpStatus.BAD_REQUEST);
    }
}
