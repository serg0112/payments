package com.example.payment.services.impl;

import com.example.payment.dao.entity.CardEntity;
import com.example.payment.dao.entity.CardHolderEntity;
import com.example.payment.dao.entity.TransactionEntity;
import com.example.payment.dao.repository.CardHolderRepository;
import com.example.payment.dao.repository.CardRepository;
import com.example.payment.dao.repository.TransactionRepository;
import com.example.payment.exception.PaymentException;
import com.example.payment.exception.PaymentExceptionResult;
import com.example.payment.services.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Objects.isNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final AuditServiceImpl auditService;
    private final CardRepository cardRepository;
    private final CardHolderRepository cardHolderRepository;
    private final TransactionRepository transactionRepository;
    private final ExecutorService auditThreadPool = Executors.newSingleThreadExecutor();

    /**
     * in JPA world this should be one transaction with locking rows in database and proper rollback strategy on exceptions
     */
    @Override
    public void saveTransaction(final TransactionEntity transactionEntity) {
        validateTransactionIsNew(transactionEntity);
        var cardHolder = findCardHolder(transactionEntity.getCardEntity().getCardHolderEntity());
        transactionEntity.getCardEntity().setCardHolderEntity(cardHolder);
        var cardEntity = findCard(transactionEntity.getCardEntity());
        transactionEntity.setCardEntity(cardEntity);
        transactionRepository.saveTransaction(transactionEntity);
        auditThreadPool.submit(() -> auditService.createAudit(transactionEntity));
    }

    @Override
    public TransactionEntity getTransaction(final String invoice) {
        return transactionRepository.findTransactionByInvoice(invoice);
    }

    private void validateTransactionIsNew(final TransactionEntity transactionEntity) {
        TransactionEntity transactionByInvoice = transactionRepository.findTransactionByInvoice(transactionEntity.getInvoice());
        if (Objects.nonNull(transactionByInvoice)) {
            throw new PaymentException(PaymentExceptionResult.INVOICE_ALREADY_EXIST, "invoice");
        }
    }

    private CardHolderEntity findCardHolder(final CardHolderEntity cardHolderEntity) {
        CardHolderEntity cardHolder = cardHolderRepository.findCardHolder(cardHolderEntity);
        if (isNull(cardHolder)) {
            final var savedCardHolder = cardHolderRepository.saveCardHolder(cardHolderEntity);
            log.info("saving new card holder with email: {}", savedCardHolder.getEmail());
            return savedCardHolder;
        }
        return cardHolder;
    }

    private CardEntity findCard(final CardEntity cardEntity) {
        CardEntity cardByPan = cardRepository.findCardByPan(cardEntity.getPan());
        if (isNull(cardByPan)) {
            log.info("saving new card: {}", cardEntity.getLastFourDigits());
            return cardRepository.saveCard(cardEntity);
        }
        return cardByPan;
    }
}
