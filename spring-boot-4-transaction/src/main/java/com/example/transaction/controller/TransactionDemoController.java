package com.example.transaction.controller;

import com.example.transaction.dto.AccountResponse;
import com.example.transaction.dto.PropagationResponse;
import com.example.transaction.dto.RollbackResponse;
import com.example.transaction.dto.SavepointResponse;
import com.example.transaction.entity.Account;
import com.example.transaction.repository.AuditLogRepository;
import com.example.transaction.service.EntityManagerExampleService;
import com.example.transaction.service.PropagationExampleService;
import com.example.transaction.service.SavepointExampleService;
import com.example.transaction.service.TransactionalExampleService;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
public class TransactionDemoController {

    private final TransactionalExampleService transactionalExampleService;
    private final EntityManagerExampleService entityManagerExampleService;
    private final SavepointExampleService savepointExampleService;
    private final PropagationExampleService propagationExampleService;
    private final AuditLogRepository auditLogRepository;

    public TransactionDemoController(
            TransactionalExampleService transactionalExampleService,
            EntityManagerExampleService entityManagerExampleService,
            SavepointExampleService savepointExampleService,
            PropagationExampleService propagationExampleService,
            AuditLogRepository auditLogRepository) {
        this.transactionalExampleService = transactionalExampleService;
        this.entityManagerExampleService = entityManagerExampleService;
        this.savepointExampleService = savepointExampleService;
        this.propagationExampleService = propagationExampleService;
        this.auditLogRepository = auditLogRepository;
    }

    @PostMapping("/transactional")
    public AccountResponse transactional(@RequestParam String owner, @RequestParam BigDecimal balance) {
        Account account = transactionalExampleService.createAccountAndLog(owner, balance);
        return toResponse(account);
    }

    @PostMapping("/rollback")
    public RollbackResponse rollback(@RequestParam String owner, @RequestParam BigDecimal balance) {
        try {
            transactionalExampleService.createAccountThenRollback(owner, balance);
        } catch (IllegalStateException ex) {
            return new RollbackResponse(true, ex.getMessage());
        }
        return new RollbackResponse(false, "Rollback did not occur");
    }

    @PostMapping("/entity-manager")
    public AccountResponse entityManager(@RequestParam String owner, @RequestParam BigDecimal balance) {
        Account account = entityManagerExampleService.createAndReload(owner, balance);
        return toResponse(account);
    }

    @PostMapping("/savepoint")
    public SavepointResponse savepoint(@RequestParam String owner) {
        return savepointExampleService.demoSavepoint(owner);
    }

    @PostMapping("/propagation")
    public PropagationResponse propagation(@RequestParam String owner) {
        boolean rolledBack = false;
        String message = "Outer transaction committed";

        try {
            propagationExampleService.demoRequiresNew(owner);
        } catch (IllegalStateException ex) {
            rolledBack = true;
            message = ex.getMessage();
        }

        long auditLogCount = auditLogRepository.count();
        return new PropagationResponse(rolledBack, auditLogCount, message);
    }

    private AccountResponse toResponse(Account account) {
        return new AccountResponse(account.getId(), account.getOwner(), account.getBalance(), account.getCreatedAt());
    }
}
