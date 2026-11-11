package com.splitsmart.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;

import java.math.BigDecimal;

public record ExpenseParticipantRequest(

        @NotNull
        @Positive
        Long userId,

        @Positive
        Long sharePaise,

        @DecimalMin(value = "0.0", inclusive = false)
        @DecimalMax("100.0")
        BigDecimal percentage

) {}