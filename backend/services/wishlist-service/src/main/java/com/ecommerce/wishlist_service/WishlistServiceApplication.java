package com.ecommerce.wishlist_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ecommerce.wishlist_service.config.DatabaseInitializer;

@SpringBootApplication
public class WishlistServiceApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(WishlistServiceApplication.class);
		app.addInitializers(new DatabaseInitializer());
		app.run(args);
	}

}
