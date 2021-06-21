package com.example.payment.services;

import com.example.payment.dao.entity.TransactionEntity;

public interface AuditService {

    void createAudit(final TransactionEntity transactionEntity);
}
