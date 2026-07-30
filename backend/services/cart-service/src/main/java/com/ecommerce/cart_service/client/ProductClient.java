package com.ecommerce.cart_service.client;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductClient {
    private final RestClient productService;
    public ProductClient(@Value("${services.product.url:http://localhost:8085}") String productServiceUrl) {
        this.productService = RestClient.builder().baseUrl(productServiceUrl).build();
    }
    public ProductSnapshot getProduct(Long productId) {
        return productService.get().uri("/products/{productId}", productId)
                .retrieve().body(ProductSnapshot.class);
    }
    public record ProductSnapshot(BigDecimal price, Inventory inventory) { }
    public record Inventory(int availableQuantity) { }
}
