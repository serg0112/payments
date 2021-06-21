package com.example.payment.dao.repository.impl;

import com.example.payment.dao.entity.TransactionEntity;
import com.example.payment.dao.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepository {

    private final Map<String, TransactionEntity> invoicesDataStore = new ConcurrentHashMap<>();

    @Override
    public void saveTransaction(final TransactionEntity transactionEntity) {
        transactionEntity.setUuid(UUID.randomUUID());
        invoicesDataStore.put(transactionEntity.getInvoice(), transactionEntity);
    }

    @Override
    public TransactionEntity findTransactionByInvoice(final String invoice) {
        return invoicesDataStore.get(invoice);
    }
}
