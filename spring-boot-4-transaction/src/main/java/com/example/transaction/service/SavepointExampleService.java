package com.example.transaction.service;

import com.example.transaction.dto.SavepointResponse;
import com.example.transaction.entity.Account;
import com.example.transaction.repository.AccountRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
public class SavepointExampleService {

    private final AccountRepository accountRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public SavepointExampleService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public SavepointResponse demoSavepoint(String owner) {
        Account first = accountRepository.save(new Account(owner + "-A", new BigDecimal("100.00")));

        TransactionStatus status = TransactionAspectSupport.currentTransactionStatus();
        Object savepoint = status.createSavepoint();
        boolean rolledBackToSavepoint = false;

        try {
            accountRepository.save(new Account(null, new BigDecimal("25.00")));
            entityManager.flush();
        } catch (DataIntegrityViolationException ex) {
            status.rollbackToSavepoint(savepoint);
            rolledBackToSavepoint = true;
        }

        Account second = accountRepository.save(new Account(owner + "-B", new BigDecimal("200.00")));
        return new SavepointResponse(first.getId(), second.getId(), rolledBackToSavepoint);
    }
}
