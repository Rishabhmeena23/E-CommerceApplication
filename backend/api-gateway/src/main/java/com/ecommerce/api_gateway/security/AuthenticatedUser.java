package com.ecommerce.api_gateway.security;

public record AuthenticatedUser(Long userId, String email) {
}
