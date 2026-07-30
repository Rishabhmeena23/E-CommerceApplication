package com.ecommerce.auth_service.dto;

public record TokenStateResponse(
        Long userId,
        String role,
        boolean active,
        Long tokenVersion) {
}
