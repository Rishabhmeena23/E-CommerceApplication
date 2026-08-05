package com.ecommerce.auth_service.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ecommerce.auth_service.dto.TokenStateResponse;
import com.ecommerce.auth_service.entity.User;
import com.ecommerce.auth_service.repository.UserRepository;

import io.swagger.v3.oas.annotations.Hidden;

/** Internal-only token-state lookup used by gateway and downstream services. */
@Hidden
@RestController
@RequestMapping("/internal/auth")
public class InternalAuthController {

    private final UserRepository userRepository;
    private final String internalServiceKey;

    public InternalAuthController(
            UserRepository userRepository,
            @Value("${internal.service.key}") String internalServiceKey) {
        this.userRepository = userRepository;
        this.internalServiceKey = internalServiceKey;
    }

    @GetMapping("/token-state/{userId}")
    public ResponseEntity<TokenStateResponse> tokenState(
            @PathVariable Long userId,
            @RequestHeader("X-Internal-Service-Key") String suppliedKey) {
        if (!internalServiceKey.equals(suppliedKey)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(new TokenStateResponse(
                user.getId(), user.getRole().name(),
                Boolean.TRUE.equals(user.getActive()), user.getTokenVersion()));
    }
}
