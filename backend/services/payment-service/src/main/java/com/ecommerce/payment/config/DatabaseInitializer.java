package com.ecommerce.payment.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Arrays;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

public class DatabaseInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext context) {
        Environment env = context.getEnvironment();
        String url = env.getProperty("spring.datasource.url");
        if (url == null) return;
        try {
            String[] parts = url.split("/");
            String database = parts[parts.length - 1].split("\\?")[0];
            String rootUrl = String.join("/", Arrays.copyOf(parts, parts.length - 1));
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(rootUrl,
                    env.getProperty("spring.datasource.username"), env.getProperty("spring.datasource.password"));
                 Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + database
                        + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Could not initialize the payment database", exception);
        }
    }
}
