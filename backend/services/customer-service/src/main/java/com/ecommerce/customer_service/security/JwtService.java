package com.ecommerce.customer_service.security;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    private final SecretKey signingKey;
    public JwtService(@Value("${jwt.secret}") String secret) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    public boolean isTokenValid(String token) {
        try { extractClaims(token); return true; } catch (Exception exception) { return false; }
    }
    public Long extractUserId(String token) {
        Number userId = extractClaims(token).get("userId", Number.class);
        return userId == null ? null : userId.longValue();
    }
    public String extractEmail(String token) { return extractClaims(token).getSubject(); }
    public String extractRole(String token) { return extractClaims(token).get("role", String.class); }
    public Long extractTokenVersion(String token) {
        Number version = extractClaims(token).get("tokenVersion", Number.class);
        return version == null ? -1L : version.longValue();
    }
    private Claims extractClaims(String token) {
        return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
    }
}
