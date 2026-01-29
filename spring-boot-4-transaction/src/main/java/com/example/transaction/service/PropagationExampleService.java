package com.example.transaction.service;

import com.example.transaction.entity.Account;
import com.example.transaction.repository.AccountRepository;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PropagationExampleService {

    private final AccountRepository accountRepository;
    private final AuditLogService auditLogService;

    public PropagationExampleService(AccountRepository accountRepository, AuditLogService auditLogService) {
        this.accountRepository = accountRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public void demoRequiresNew(String owner) {
        accountRepository.save(new Account(owner, new BigDecimal("300.00")));
        auditLogService.logRequiresNew("Logged account creation for " + owner);
        throw new IllegalStateException("Simulated failure to roll back the outer transaction");
    }
}
