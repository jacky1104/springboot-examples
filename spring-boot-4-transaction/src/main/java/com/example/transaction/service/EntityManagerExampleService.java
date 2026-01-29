package com.example.transaction.service;

import com.example.transaction.entity.Account;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EntityManagerExampleService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Account createAndReload(String owner, BigDecimal balance) {
        Account account = new Account(owner, balance);
        entityManager.persist(account);
        entityManager.flush();
        entityManager.clear();
        return entityManager.find(Account.class, account.getId());
    }
}
