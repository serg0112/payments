package com.example.payment.dao.repository;

import com.example.payment.dao.entity.TransactionEntity;

public interface TransactionRepository {

    /**
     * In JPA world should be transaction method. Save a transaction or roll it back if any exception
     *
     * @param transactionEntity
     */
    void saveTransaction(TransactionEntity transactionEntity);

    TransactionEntity findTransactionByInvoice(String invoice);
}
