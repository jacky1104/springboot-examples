package com.example.transaction.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record AccountResponse(Long id, String owner, BigDecimal balance, Instant createdAt) {
}
