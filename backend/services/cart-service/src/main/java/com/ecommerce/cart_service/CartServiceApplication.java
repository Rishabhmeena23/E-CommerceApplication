package com.ecommerce.cart_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ecommerce.cart_service.config.DatabaseInitializer;

@SpringBootApplication
public class CartServiceApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(CartServiceApplication.class);
		app.addInitializers(new DatabaseInitializer());
		app.run(args);
	}

}
