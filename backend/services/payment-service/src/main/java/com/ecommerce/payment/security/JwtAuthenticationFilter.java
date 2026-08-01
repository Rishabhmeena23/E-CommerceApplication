package com.ecommerce.payment.security;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwt; private final RestClient auth; private final String serviceKey;
    public JwtAuthenticationFilter(JwtService jwt, @Value("${services.auth.url}") String authUrl,
            @Value("${internal.service.key}") String serviceKey) {
        this.jwt = jwt; this.auth = RestClient.builder().baseUrl(authUrl).build(); this.serviceKey = serviceKey;
    }
    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ") && SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = header.substring(7);
            if (jwt.isValid(token)) {
                Long userId = jwt.userId(token); String role = jwt.role(token); TokenState state = state(userId);
                if (state != null && state.active() && role.equals(state.role()) && jwt.tokenVersion(token).equals(state.tokenVersion())) {
                    AuthenticatedUser principal = new AuthenticatedUser(userId, jwt.email(token), role);
                    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                            principal, null, List.of(new SimpleGrantedAuthority("ROLE_" + role))));
                }
            }
        }
        chain.doFilter(request, response);
    }
    private TokenState state(Long userId) { try {
        return auth.get().uri("/internal/auth/token-state/{id}", userId).header("X-Internal-Service-Key", serviceKey)
                .retrieve().body(TokenState.class);
    } catch (Exception ignored) { return null; } }
    private record TokenState(Long userId, String role, boolean active, Long tokenVersion) { }
}
