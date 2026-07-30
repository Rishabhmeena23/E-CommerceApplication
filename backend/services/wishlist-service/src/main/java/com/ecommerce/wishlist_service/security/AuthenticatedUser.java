package com.ecommerce.wishlist_service.security;

public record AuthenticatedUser(Long userId, String email) { }
