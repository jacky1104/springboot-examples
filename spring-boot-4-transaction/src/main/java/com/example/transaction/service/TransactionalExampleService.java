package com.example.transaction.service;

import com.example.transaction.entity.Account;
import com.example.transaction.entity.AuditLog;
import com.example.transaction.repository.AccountRepository;
import com.example.transaction.repository.AuditLogRepository;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionalExampleService {

    private final AccountRepository accountRepository;
    private final AuditLogRepository auditLogRepository;

    public TransactionalExampleService(AccountRepository accountRepository, AuditLogRepository auditLogRepository) {
        this.accountRepository = accountRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public Account createAccountAndLog(String owner, BigDecimal balance) {
        Account account = accountRepository.save(new Account(owner, balance));
        auditLogRepository.save(new AuditLog("Created account for " + owner));
        return account;
    }

    @Transactional
    public void createAccountThenRollback(String owner, BigDecimal balance) {
        accountRepository.save(new Account(owner, balance));
        auditLogRepository.save(new AuditLog("Attempted account creation for " + owner));
        throw new IllegalStateException("Simulated rollback");
    }
}
