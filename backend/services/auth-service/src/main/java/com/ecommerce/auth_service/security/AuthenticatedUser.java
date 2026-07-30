package com.ecommerce.auth_service.security;

/** The trusted identity extracted from a validated access token. */
public record AuthenticatedUser(Long userId, String email) {
}
