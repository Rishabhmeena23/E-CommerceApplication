package com.ecommerce.cart_service.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatabaseInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        Environment env = applicationContext.getEnvironment();
        String dbUrl = env.getProperty("spring.datasource.url");
        String dbUsername = env.getProperty("spring.datasource.username");
        String dbPassword = env.getProperty("spring.datasource.password");

        if (dbUrl != null) {
            initializeDatabase(dbUrl, dbUsername, dbPassword);
        }
    }

    public static void initializeDatabase(String dbUrl, String dbUsername, String dbPassword) {
        try {
            String[] urlParts = dbUrl.split("/");
            String databaseName = urlParts[urlParts.length - 1].split("\\?")[0];
            String rootUrl = String.join("/", java.util.Arrays.copyOf(urlParts, urlParts.length - 1));

            log.info("Creating database: {}", databaseName);

            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(rootUrl, dbUsername, dbPassword);
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + databaseName + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
                log.info("Database {} created or already exists", databaseName);
            }
        } catch (Exception e) {
            log.error("Failed to initialize database", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }
}
