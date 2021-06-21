package com.example.payment.services.impl;

import com.example.payment.dao.entity.TransactionEntity;
import com.example.payment.mapper.TransactionMapper;
import com.example.payment.services.AuditService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.NonWritableChannelException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    @Value("${log.audit.file.path}")
    private String filePath;
    private final ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    private final  ReentrantLock lock = new ReentrantLock();

    @Override
    public void createAudit(final TransactionEntity transactionEntity) {
        try {
            var transaction = ow.writeValueAsString(TransactionMapper.mapTransactionEntityToPaymentDto(transactionEntity));
            writeAuditToFile(transaction);
        } catch (JsonProcessingException e) {
            log.error("could not write an audit for invoice: {}", transactionEntity.getInvoice(), e);
        }
    }

    private void writeAuditToFile(final String transaction) {
        //Write JSON file
        lock.lock();
        var path = Path.of(filePath);
        try {
            Files.writeString(path, transaction,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND,
                    StandardOpenOption.WRITE);
        } catch (IOException e) {
            log.error("could not write an audit to a file", e);
        }
        finally {
            lock.unlock();
        }
    }
}
