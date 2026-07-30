package com.ecommerce.cart_service.security;

public record AuthenticatedUser(Long userId, String email) { }
