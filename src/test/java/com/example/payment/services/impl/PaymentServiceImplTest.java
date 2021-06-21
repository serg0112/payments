package com.example.payment.services.impl;

import com.example.payment.dao.entity.CardEntity;
import com.example.payment.dao.entity.CardHolderEntity;
import com.example.payment.dao.entity.TransactionEntity;
import com.example.payment.dao.repository.CardHolderRepository;
import com.example.payment.dao.repository.CardRepository;
import com.example.payment.dao.repository.TransactionRepository;
import com.example.payment.exception.PaymentException;
import com.example.payment.exception.PaymentExceptionResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private AuditServiceImpl auditService;
    @Mock
    private CardRepository cardRepository;
    @Mock
    private CardHolderRepository cardHolderRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @InjectMocks
    private PaymentServiceImpl paymentService;


    @Test
    void givenRequestToGetTransaction_whenTransactionExist_thenTransactionIsReturned() {

        final var transactionEntity = getTransactionEntity();
        when(transactionRepository.findTransactionByInvoice(transactionEntity.getInvoice())).thenReturn(transactionEntity);

        final var paymentServiceTransaction = paymentService.getTransaction(transactionEntity.getInvoice());

        Assertions.assertEquals(transactionEntity, paymentServiceTransaction);
    }

    @Test
    void givenRequestToGetTransaction_whenTransactionNotExist_thenNullIsReturned() {

        final var transactionEntity = getTransactionEntity();
        when(transactionRepository.findTransactionByInvoice(transactionEntity.getInvoice())).thenReturn(null);

        final var paymentServiceTransaction = paymentService.getTransaction(transactionEntity.getInvoice());

        Assertions.assertNull(paymentServiceTransaction);
    }

    @Test
    void givenRequestToSaveTransaction_whenTransactionNotExist_thenTransactionIsSaved() {

        final var transactionEntity = getTransactionEntity();
        when(cardRepository.findCardByPan(transactionEntity.getCardEntity().getPan())).thenReturn(transactionEntity.getCardEntity());
        when(cardHolderRepository.findCardHolder(transactionEntity.getCardEntity().getCardHolderEntity())).thenReturn(transactionEntity.getCardEntity().getCardHolderEntity());
        when(transactionRepository.findTransactionByInvoice(transactionEntity.getInvoice())).thenReturn(null);

        paymentService.saveTransaction(transactionEntity);

        verify(transactionRepository, times(1)).saveTransaction(transactionEntity);
        verify(auditService, times(1)).createAudit(transactionEntity);
    }


    @Test
    void givenTransactionWithExistedInvoice_whenTransactionExist_thenTransactionExceptionIsThrown() {
        final var transactionEntity = getTransactionEntity();
        when(transactionRepository.findTransactionByInvoice(transactionEntity.getInvoice())).thenReturn(transactionEntity);
        try {
            paymentService.saveTransaction(transactionEntity);
            Assertions.fail("Exception is not thrown when expected");
        } catch (PaymentException e) {
            Assertions.assertEquals(PaymentExceptionResult.INVOICE_ALREADY_EXIST ,e.getResult());
        }
    }


    private static TransactionEntity getTransactionEntity() {
        CardHolderEntity cardHolderEntity = new CardHolderEntity();
        cardHolderEntity.setEmail("test@test.com");
        cardHolderEntity.setName("test");

        CardEntity cardEntity = new CardEntity();
        cardEntity.setCardHolderEntity(cardHolderEntity);
        cardEntity.setExpiry("1225");
        cardEntity.setPan("5555555555554444");
        cardEntity.setLastFourDigits("4444");

        TransactionEntity txEntity = new TransactionEntity();
        txEntity.setCurrency("USD");
        txEntity.setAmount("123");
        txEntity.setInvoice("123");
        txEntity.setCardEntity(cardEntity);

        return txEntity;
    }
}