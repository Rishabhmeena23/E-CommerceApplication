package com.ecomm.productservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ecomm.productservice.config.DatabaseInitializer;

@SpringBootApplication
public class Product_Service_Application {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Product_Service_Application.class);
        app.addInitializers(new DatabaseInitializer());
        app.run(args);
    }

}
