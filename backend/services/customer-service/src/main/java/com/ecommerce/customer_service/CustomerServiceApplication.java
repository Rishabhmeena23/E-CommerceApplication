package com.ecommerce.customer_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ecommerce.customer_service.config.DatabaseInitializer;

@SpringBootApplication
public class CustomerServiceApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(CustomerServiceApplication.class);
		app.addInitializers(new DatabaseInitializer());
		app.run(args);
	}

}
