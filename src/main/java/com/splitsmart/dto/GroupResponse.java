package com.splitsmart.dto;

import java.time.LocalDateTime;

public record GroupResponse(
        Long id,
        String name,
        Long createdById,
        LocalDateTime createdAt
) {}