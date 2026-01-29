# Spring Boot 4.0.2 Transaction Examples

This project demonstrates Spring Boot 4.0.2 with Gradle 9.2.1 and showcases common transaction patterns.

## Key Features

- Spring Boot 4.0.2
- Gradle 9.2.1
- @Transactional example
- EntityManager example
- Savepoint example
- Transaction propagation example (REQUIRES_NEW)
- H2 in-memory database

## Running the Application

```bash
./gradlew bootRun
```

## API Endpoints

### 1) @Transactional example
```
POST http://localhost:8080/api/transactions/transactional?owner=Alice&balance=100.00
```

### 2) EntityManager example
```
POST http://localhost:8080/api/transactions/entity-manager?owner=Bob&balance=250.00
```

### 3) Savepoint example
```
POST http://localhost:8080/api/transactions/savepoint?owner=Carol
```

### 4) Transaction propagation example
```
POST http://localhost:8080/api/transactions/propagation?owner=Dave
```

The propagation endpoint intentionally throws an exception in the outer transaction to show that the audit log saved with `REQUIRES_NEW` still commits.

## H2 Console

The H2 console is enabled at `http://localhost:8080/h2-console` with JDBC URL `jdbc:h2:mem:txdb`.
