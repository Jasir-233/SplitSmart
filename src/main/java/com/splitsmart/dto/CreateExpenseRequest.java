package com.splitsmart.dto;

import com.splitsmart.entity.SplitType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public record CreateExpenseRequest(

        @NotBlank
        @Size(max = 200)
        String description,

        @NotNull
        @Positive
        Long amountPaise,

        @NotNull
        @Positive
        Long paidByUserId,

        @NotNull
        SplitType splitType,

        @NotNull
        LocalDate expenseDate,

        @NotEmpty
        @Valid
        List<ExpenseParticipantRequest> participants

) {}