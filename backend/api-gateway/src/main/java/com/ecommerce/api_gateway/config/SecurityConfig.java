package com.ecommerce.api_gateway.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.ecommerce.api_gateway.security.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Enable CORS
                .cors(cors -> {})

                // Disable CSRF
                .csrf(csrf -> csrf.disable())

                // Stateless session
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Authorization Rules
                .authorizeHttpRequests(auth -> auth

                        // OpenAPI documentation and the central Swagger UI.
                        .requestMatchers(
                                path("/swagger-ui.html"),
                                path("/swagger-ui/**"),
                                path("/v3/api-docs/**"),
                                path("/openapi/**"))
                        .permitAll()

                        // Public APIs
                        .requestMatchers(path("/auth/**")).permitAll()
                        .requestMatchers(path("/error")).permitAll()

                        // Browsing the catalog is public; write operations are role-based.
                        .requestMatchers(
                                path(org.springframework.http.HttpMethod.GET, "/products/**"),
                                path(org.springframework.http.HttpMethod.GET, "/categories/**"),
                                path(org.springframework.http.HttpMethod.GET, "/subcategories/**"))
                        .permitAll()
                        .requestMatchers(
                                path(org.springframework.http.HttpMethod.POST, "/categories/**"),
                                path(org.springframework.http.HttpMethod.POST, "/subcategories/**"))
                        .hasRole("ADMIN")
                        .requestMatchers(
                                path(org.springframework.http.HttpMethod.PUT, "/categories/**"),
                                path(org.springframework.http.HttpMethod.PUT, "/subcategories/**"))
                        .hasRole("ADMIN")
                        .requestMatchers(
                                path(org.springframework.http.HttpMethod.DELETE, "/categories/**"),
                                path(org.springframework.http.HttpMethod.DELETE, "/subcategories/**"))
                        .hasRole("ADMIN")
                        .requestMatchers(path(org.springframework.http.HttpMethod.POST,
                                "/products/**"))
                        .hasAnyRole("SELLER", "ADMIN")
                        .requestMatchers(path(org.springframework.http.HttpMethod.PUT,
                                "/products/**"))
                        .hasAnyRole("SELLER", "ADMIN")
                        .requestMatchers(path(org.springframework.http.HttpMethod.PATCH,
                                "/products/**"))
                        .hasAnyRole("SELLER", "ADMIN")
                        .requestMatchers(path(org.springframework.http.HttpMethod.DELETE,
                                "/products/**"))
                        .hasAnyRole("SELLER", "ADMIN")

                        .requestMatchers(path(org.springframework.http.HttpMethod.PATCH,
                                "/sellers/*/status"))
                        .hasRole("ADMIN")
                        .requestMatchers(path(org.springframework.http.HttpMethod.GET,
                                "/sellers"))
                        .hasRole("ADMIN")
                        .requestMatchers(path(org.springframework.http.HttpMethod.GET,
                                "/sellers/user/*"))
                        .hasRole("ADMIN")

                        // Profile self-service remains available after a user
                        // is promoted from CUSTOMER to SELLER or ADMIN.
                        .requestMatchers(path("/customers/me/**"))
                        .authenticated()
                        .requestMatchers(path(org.springframework.http.HttpMethod.GET,
                                "/customers"))
                        .hasRole("ADMIN")
                        .requestMatchers(path("/customers/*"))
                        .hasRole("ADMIN")

                        // Admin APIs
                        .requestMatchers(path("/admin/**")).hasRole("ADMIN")

                        // User Management APIs
                        .requestMatchers(path("/users/**")).hasRole("ADMIN")

                        // Orders and payments are private. Collection reads and
                        // order fulfilment controls belong to administrators.
                        .requestMatchers(path(org.springframework.http.HttpMethod.GET, "/orders"))
                        .hasRole("ADMIN")
                        .requestMatchers(path(org.springframework.http.HttpMethod.PATCH, "/orders/*/status"))
                        .hasRole("ADMIN")
                        .requestMatchers(path(org.springframework.http.HttpMethod.GET, "/payments"))
                        .hasRole("ADMIN")
                        .requestMatchers(path("/orders/**"), path("/payments/**"))
                        .hasAnyRole("CUSTOMER", "SELLER", "ADMIN")

                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )

                // JWT Filter
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    private static PathPatternRequestMatcher path(String pattern) {
        return PathPatternRequestMatcher.pathPattern(pattern);
    }

    private static PathPatternRequestMatcher path(
            org.springframework.http.HttpMethod method,
            String pattern) {
        return PathPatternRequestMatcher.pathPattern(method, pattern);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of("http://localhost:5173", "http://127.0.0.1:5173"));

        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        configuration.setAllowedHeaders(
                List.of("*"));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
