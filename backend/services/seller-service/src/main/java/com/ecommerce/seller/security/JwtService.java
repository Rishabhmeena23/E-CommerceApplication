package com.ecommerce.seller.security;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
@Service public class JwtService {
 private final SecretKey signingKey; public JwtService(@Value("${jwt.secret}") String secret){ signingKey=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); }
 public boolean isTokenValid(String token){try{claims(token);return true;}catch(Exception e){return false;}}
 public Long extractUserId(String token){Number n=claims(token).get("userId",Number.class);return n==null?null:n.longValue();} public String extractEmail(String t){return claims(t).getSubject();} public String extractRole(String t){return claims(t).get("role",String.class);} public Long extractTokenVersion(String t){Number n=claims(t).get("tokenVersion",Number.class);return n==null?-1L:n.longValue();} private Claims claims(String t){return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(t).getPayload();}
}
