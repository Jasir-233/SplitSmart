package com.splitsmart.dto;

import com.splitsmart.entity.SplitType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ExpenseResponse(
        Long id,
        Long groupId,
        String description,
        long amountPaise,
        Long paidByUserId,
        Long createdByUserId,
        SplitType splitType,
        LocalDate expenseDate,
        LocalDateTime createdAt,
        //new
        List<ExpenseParticipantResponse> participants
) {
}