package com.ecommerce.auth_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ecommerce.auth_service.config.DatabaseInitializer;

@SpringBootApplication
public class AuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(AuthServiceApplication.class);
		app.addInitializers(new DatabaseInitializer());
		app.run(args);
	}

}
