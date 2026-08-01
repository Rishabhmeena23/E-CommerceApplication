package com.ecommerce.payment.security;

public record AuthenticatedUser(Long userId, String email, String role) { }
