package com.ecommerce.customer_service.security;

public record AuthenticatedUser(Long userId, String email) {
}
