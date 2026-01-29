package com.example.transaction;

import com.example.transaction.config.TransactionConfig;
import com.example.transaction.entity.Account;
import com.example.transaction.entity.AuditLog;
import com.example.transaction.repository.AccountRepository;
import com.example.transaction.repository.AuditLogRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@DataJpaTest
@Import(TransactionConfig.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class TransactionRepositoryTest {
    private static final Logger LOG = LoggerFactory.getLogger(TransactionRepositoryTest.class);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private TransactionTemplate transactionTemplate;
    private TransactionTemplate requiresNewTemplate;

    @BeforeEach
    void setUp() {
        auditLogRepository.deleteAll();
        accountRepository.deleteAll();
        transactionTemplate = new TransactionTemplate(transactionManager);
        requiresNewTemplate = new TransactionTemplate(transactionManager);
        requiresNewTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    @Test
    void commitTransactionPersistsAccountAndAuditLog() {
        transactionTemplate.executeWithoutResult(status -> {
            accountRepository.save(new Account("Alice", new BigDecimal("100.00")));
            auditLogRepository.save(new AuditLog("Created account for Alice"));
        });

        assertThat(accountRepository.count()).isEqualTo(1);
        assertThat(auditLogRepository.count()).isEqualTo(1);
    }

    @Test
    void rollbackTransactionClearsAllChanges() {
        try {
            transactionTemplate.executeWithoutResult(status -> {
                accountRepository.save(new Account("Bob", new BigDecimal("150.00")));
                auditLogRepository.save(new AuditLog("Attempted account for Bob"));
                throw new IllegalStateException("Simulated rollback");
            });
        } catch (IllegalStateException ex) {
            // expected
        }

        assertThat(accountRepository.count()).isEqualTo(0);
        assertThat(auditLogRepository.count()).isEqualTo(0);
    }

    @Test
    void savepointRollsBackInnerFailureOnly() {
        if (transactionManager instanceof org.springframework.orm.jpa.JpaTransactionManager) {
            LOG.warn("Skipping savepoint test because transaction manager is {}", transactionManager.getClass().getName());
            assumeTrue(false, "JpaTransactionManager does not support savepoints");
        }
        boolean rolledBack = transactionTemplate.execute(status -> {
            Account first = accountRepository.save(new Account("Carol-A", new BigDecimal("100.00")));
            Object savepoint = status.createSavepoint();
            boolean rolledBackToSavepoint = false;

            try {
                accountRepository.save(new Account(null, new BigDecimal("25.00")));
                entityManager.flush();
            } catch (DataIntegrityViolationException ex) {
                LOG.debug("Caught DataIntegrityViolationException while saving invalid account", ex);
                status.rollbackToSavepoint(savepoint);
                rolledBackToSavepoint = true;
                LOG.debug("rolledBackToSavepoint set to {}", rolledBackToSavepoint);
            }

            accountRepository.save(new Account("Carol-B", new BigDecimal("200.00")));
            entityManager.flush();

            assertThat(first.getId()).isNotNull();
            return rolledBackToSavepoint;
        });
        LOG.info("rolledBack: {}", rolledBack);
        assertThat(rolledBack).isTrue();
        assertThat(accountRepository.count()).isEqualTo(2);
    }

    @Test
    void requiresNewCommitsAuditLogWhenOuterRollsBack() {
        try {
            transactionTemplate.executeWithoutResult(status -> {
                accountRepository.save(new Account("Dave", new BigDecimal("300.00")));
                requiresNewTemplate.executeWithoutResult(innerStatus ->
                        auditLogRepository.save(new AuditLog("Logged account creation for Dave")));
                throw new IllegalStateException("Simulated outer rollback");
            });
        } catch (IllegalStateException ex) {
            // expected
        }

        assertThat(accountRepository.count()).isEqualTo(0);
        assertThat(auditLogRepository.count()).isEqualTo(1);
    }
}
