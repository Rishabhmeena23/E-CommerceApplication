package com.ecommerce.seller.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        String datasourceUrl = applicationContext.getEnvironment()
                .getProperty("spring.datasource.url");
        String datasourceUsername = applicationContext.getEnvironment()
                .getProperty("spring.datasource.username");
        String datasourcePassword = applicationContext.getEnvironment()
                .getProperty("spring.datasource.password");

        if (datasourceUrl != null && datasourceUsername != null && datasourcePassword != null) {
            createDatabaseIfNotExists(datasourceUrl, datasourceUsername, datasourcePassword);
        }
    }

    private static void createDatabaseIfNotExists(String datasourceUrl, String username, String password) {
        try {
            String databaseName = extractDatabaseName(datasourceUrl);
            String rootUrl = datasourceUrl.substring(0, datasourceUrl.lastIndexOf('/'));

            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(rootUrl, username, password);
                 Statement statement = connection.createStatement()) {
                statement.executeUpdate(
                        "CREATE DATABASE IF NOT EXISTS " + databaseName
                                + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci"
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database: " + e.getMessage(), e);
        }
    }

    private static String extractDatabaseName(String datasourceUrl) {
        int lastSlash = datasourceUrl.lastIndexOf('/');
        int questionMark = datasourceUrl.indexOf('?', lastSlash);
        if (questionMark != -1) {
            return datasourceUrl.substring(lastSlash + 1, questionMark);
        }
        return datasourceUrl.substring(lastSlash + 1);
    }
}
