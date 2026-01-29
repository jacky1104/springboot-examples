package com.example.transaction.dto;

public record RollbackResponse(boolean rolledBack, String message) {
}
