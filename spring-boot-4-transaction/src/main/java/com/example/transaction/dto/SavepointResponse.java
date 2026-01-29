package com.example.transaction.dto;

public record SavepointResponse(Long firstAccountId, Long secondAccountId, boolean rolledBackToSavepoint) {
}
