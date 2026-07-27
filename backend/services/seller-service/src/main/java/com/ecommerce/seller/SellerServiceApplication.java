package com.ecommerce.seller;

import com.ecommerce.seller.config.DatabaseInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SellerServiceApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SellerServiceApplication.class);
        app.addInitializers(new DatabaseInitializer());
        app.run(args);
    }

}
