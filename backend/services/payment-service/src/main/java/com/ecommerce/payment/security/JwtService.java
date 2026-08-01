package com.ecommerce.payment.security;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    private final SecretKey key;
    public JwtService(@Value("${jwt.secret}") String secret) { this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); }
    public boolean isValid(String token) { try { claims(token); return true; } catch (Exception ignored) { return false; } }
    public Long userId(String token) { Number value = claims(token).get("userId", Number.class); return value == null ? null : value.longValue(); }
    public Long tokenVersion(String token) { Number value = claims(token).get("tokenVersion", Number.class); return value == null ? -1 : value.longValue(); }
    public String email(String token) { return claims(token).getSubject(); }
    public String role(String token) { return claims(token).get("role", String.class); }
    private Claims claims(String token) { return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload(); }
}
