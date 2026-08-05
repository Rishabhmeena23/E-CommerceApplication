package com.ecommerce.auth_service.config;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Mercato Auth Service API",
                version = "1.0",
                description = "Registration, login and administrator-controlled user management"),
        servers = @Server(url = "/"))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT")
public class OpenApiConfig {

    @Bean
    OpenApiCustomizer secureUserManagementApis() {
        return openApi -> openApi.getPaths().forEach((path, pathItem) -> {
            if (path.startsWith("/users")) {
                pathItem.readOperations().forEach(operation -> operation.addSecurityItem(
                        new SecurityRequirement().addList("bearerAuth")));
            }
        });
    }
}
