package com.ecommerce.api_gateway.security;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
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

    public JwtAuthenticationFilter(
            JwtService jwtService,
            @Value("${services.auth.url:http://localhost:8081}") String authServiceUrl,
            @Value("${internal.service.key}") String internalServiceKey) {
        this.jwtService = jwtService;
        this.authServiceClient = RestClient.builder().baseUrl(authServiceUrl).build();
        this.internalServiceKey = internalServiceKey;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader =
                request.getHeader(HttpHeaders.AUTHORIZATION);

        // No JWT provided.
        // Continue the filter chain and let SecurityConfig decide
        // whether this particular endpoint is public or protected.
        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);

        if (jwtService.isTokenValid(token)
                && SecurityContextHolder.getContext()
                        .getAuthentication() == null) {

            Long userId = jwtService.extractUserId(token);
            String email = jwtService.extractEmail(token);
            String role = jwtService.extractRole(token);
            Long tokenVersion = jwtService.extractTokenVersion(token);

            TokenState tokenState = getTokenState(userId);
            if (tokenState == null || !tokenState.active()
                    || !role.equals(tokenState.role())
                    || !tokenVersion.equals(tokenState.tokenVersion())) {
                filterChain.doFilter(request, response);
                return;
            }

            SimpleGrantedAuthority authority =
                    new SimpleGrantedAuthority(
                            "ROLE_" + role
                    );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            new AuthenticatedUser(userId, email),
                            null,
                            List.of(authority)
                    );

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private TokenState getTokenState(Long userId) {
        try {
            return authServiceClient.get()
                    .uri("/internal/auth/token-state/{userId}", userId)
                    .header("X-Internal-Service-Key", internalServiceKey)
                    .retrieve()
                    .body(TokenState.class);
        } catch (Exception exception) {
            return null;
        }
    }

    private record TokenState(
            Long userId, String role, boolean active, Long tokenVersion) {
    }
}
