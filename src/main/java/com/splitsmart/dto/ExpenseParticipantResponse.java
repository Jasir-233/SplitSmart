package com.splitsmart.dto;

public record ExpenseParticipantResponse(
        Long userId,
        String name,
        long sharePaise
) {
}