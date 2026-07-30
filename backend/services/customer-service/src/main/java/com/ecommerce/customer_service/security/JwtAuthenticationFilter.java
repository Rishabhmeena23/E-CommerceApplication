package com.ecommerce.customer_service.security;

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
    private final JwtService jwtService;
    private final RestClient authServiceClient;
    private final String internalServiceKey;
    public JwtAuthenticationFilter(JwtService jwtService,
            @Value("${services.auth.url:http://localhost:8081}") String authServiceUrl,
            @Value("${internal.service.key}") String internalServiceKey) {
        this.jwtService = jwtService;
        this.authServiceClient = RestClient.builder().baseUrl(authServiceUrl).build();
        this.internalServiceKey = internalServiceKey;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); return;
        }
        String token = authorization.substring(7);
        if (jwtService.isTokenValid(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
            Long userId = jwtService.extractUserId(token);
            String role = jwtService.extractRole(token);
            TokenState state = userId == null ? null : tokenState(userId);
            if (state != null && state.active() && role.equals(state.role())
                    && jwtService.extractTokenVersion(token).equals(state.tokenVersion())) {
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                        new AuthenticatedUser(userId, jwtService.extractEmail(token)), null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + state.role()))));
            }
        }
        filterChain.doFilter(request, response);
    }
    private TokenState tokenState(Long userId) {
        try {
            return authServiceClient.get().uri("/internal/auth/token-state/{userId}", userId)
                    .header("X-Internal-Service-Key", internalServiceKey).retrieve().body(TokenState.class);
        } catch (Exception exception) { return null; }
    }
    private record TokenState(Long userId, String role, boolean active, Long tokenVersion) { }
}
