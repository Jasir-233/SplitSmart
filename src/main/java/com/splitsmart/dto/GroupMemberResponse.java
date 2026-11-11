package com.splitsmart.dto;

public record GroupMemberResponse(
        Long userId,
        String name,
        String email
) {
}