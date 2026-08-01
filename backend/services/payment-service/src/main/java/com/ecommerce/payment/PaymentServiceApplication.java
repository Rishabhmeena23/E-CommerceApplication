package com.ecommerce.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.ecommerce.payment.config.DatabaseInitializer;

@SpringBootApplication
public class PaymentServiceApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(PaymentServiceApplication.class);
        app.addInitializers(new DatabaseInitializer());
        app.run(args);
    }
}
