package com.example.payment.services;

import com.example.payment.dao.entity.TransactionEntity;

public interface PaymentService {

    void saveTransaction(TransactionEntity transactionEntity);

    TransactionEntity getTransaction(String invoice);
}
