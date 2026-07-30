package com.ecommerce.seller.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;
import com.ecommerce.seller.security.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/sellers/me").authenticated()
                        .requestMatchers(HttpMethod.POST, "/sellers").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.PATCH, "/sellers/*/status").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/sellers").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/sellers/user/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/sellers/*").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/sellers/**").hasAnyRole("CUSTOMER", "SELLER")
                        .requestMatchers(HttpMethod.DELETE, "/sellers/**").hasAnyRole("CUSTOMER", "SELLER")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
