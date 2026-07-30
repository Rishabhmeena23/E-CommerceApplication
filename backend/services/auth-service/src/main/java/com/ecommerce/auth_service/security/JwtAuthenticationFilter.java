package com.ecommerce.auth_service.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ecommerce.auth_service.entity.User;
import com.ecommerce.auth_service.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader =
                request.getHeader("Authorization");

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
            Long tokenVersion = jwtService.extractTokenVersion(token);

            userRepository.findById(userId)
                    .filter(user -> Boolean.TRUE.equals(user.getActive()))
                    .filter(user -> user.getTokenVersion().equals(tokenVersion))
                    .ifPresent(user -> authenticate(user));
        }

        filterChain.doFilter(request, response);
    }

    private void authenticate(User user) {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(
                "ROLE_" + user.getRole().name());
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        new AuthenticatedUser(user.getId(), user.getEmail()),
                        null,
                        List.of(authority));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
