package com.example.transaction;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class SavepointJdbcTransactionTest {

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;
    private TransactionTemplate transactionTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate = new JdbcTemplate(dataSource);
        transactionTemplate = new TransactionTemplate(new DataSourceTransactionManager(dataSource));
        jdbcTemplate.update("DELETE FROM audit_logs");
        jdbcTemplate.update("DELETE FROM accounts");
    }

    @Test
    void savepointRollsBackInnerFailureOnly() {
        Boolean rolledBack = transactionTemplate.execute(status -> {
            jdbcTemplate.update(
                    "INSERT INTO accounts (balance, created_at, owner) VALUES (?, ?, ?)",
                    new BigDecimal("100.00"),
                    Timestamp.from(Instant.now()),
                    "Eve-A");
            Object savepoint = status.createSavepoint();
            boolean rolledBackToSavepoint = false;

            try {
                jdbcTemplate.update(
                        "INSERT INTO accounts (balance, created_at, owner) VALUES (?, ?, ?)",
                        new BigDecimal("25.00"),
                        Timestamp.from(Instant.now()),
                        null);
            } catch (DataIntegrityViolationException ex) {
                status.rollbackToSavepoint(savepoint);
                rolledBackToSavepoint = true;
            }

            jdbcTemplate.update(
                    "INSERT INTO accounts (balance, created_at, owner) VALUES (?, ?, ?)",
                    new BigDecimal("200.00"),
                    Timestamp.from(Instant.now()),
                    "Eve-B");
            return rolledBackToSavepoint;
        });

        assertThat(rolledBack).isTrue();
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM accounts", Integer.class);
        assertThat(count).isEqualTo(2);
    }
}
