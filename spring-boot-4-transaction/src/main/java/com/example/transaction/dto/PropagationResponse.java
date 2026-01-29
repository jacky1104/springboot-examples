package com.example.transaction.dto;

public record PropagationResponse(boolean outerRolledBack, long auditLogCount, String message) {
}
