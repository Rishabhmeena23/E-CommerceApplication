package com.ecommerce.admin_service.client.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;

import com.ecommerce.admin_service.client.AuthServiceClient;
import com.ecommerce.admin_service.dto.RoleRequest;
import com.ecommerce.admin_service.dto.UserDto;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class WebClientAuthServiceClient implements AuthServiceClient {

    private final WebClient webClient;

    public WebClientAuthServiceClient(
            WebClient.Builder webClientBuilder,
            @Value("${services.auth.url}") String authServiceUrl) {

        this.webClient = webClientBuilder
                .baseUrl(authServiceUrl)
                .build();
    }

    private String getAuthorizationHeader() {

        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder
                        .getRequestAttributes();

        if (attributes == null) {
            throw new IllegalStateException(
                    "No current HTTP request available");
        }

        HttpServletRequest request = attributes.getRequest();

        String authorization =
                request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorization == null ||
                !authorization.startsWith("Bearer ")) {

            throw new IllegalStateException(
                    "Bearer token is missing");
        }

        return authorization;
    }

    @Override
    public List<UserDto> getAllUsers() {

        return webClient.get()
                .uri("/users")
                .header(
                        HttpHeaders.AUTHORIZATION,
                        getAuthorizationHeader()
                )
                .retrieve()
                .bodyToMono(
                        new ParameterizedTypeReference<List<UserDto>>() {}
                )
                .block();
    }

    @Override
    public UserDto getUserById(Long id) {

        return webClient.get()
                .uri("/users/{id}", id)
                .header(
                        HttpHeaders.AUTHORIZATION,
                        getAuthorizationHeader()
                )
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();
    }

    @Override
    public void banUser(Long id, String reason) {

        webClient.patch()
                .uri("/users/{id}/ban", id)
                .header(
                        HttpHeaders.AUTHORIZATION,
                        getAuthorizationHeader()
                )
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();
    }

    @Override
    public void unbanUser(Long id) {

        webClient.patch()
                .uri("/users/{id}/unban", id)
                .header(
                        HttpHeaders.AUTHORIZATION,
                        getAuthorizationHeader()
                )
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();
    }

    @Override
    public void assignRole(Long id, String role) {

        RoleRequest request = new RoleRequest(role);

        webClient.patch()
                .uri("/users/{id}/role", id)
                .header(
                        HttpHeaders.AUTHORIZATION,
                        getAuthorizationHeader()
                )
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();
    }
}