package com.ecommerce.order.security;

public record AuthenticatedUser(Long userId, String email, String role) { }
