package com.ecommerce.order.client;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ProductClient {
    private final RestClient client;
    public ProductClient(@Value("${services.product.url}") String url) { this.client = RestClient.builder().baseUrl(url).build(); }
    public ProductSnapshot get(Long productId) {
        try { return client.get().uri("/products/{id}", productId).retrieve().body(ProductSnapshot.class); }
        catch (Exception exception) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product " + productId + " is unavailable"); }
    }
    public record ProductSnapshot(Long id, String name, BigDecimal price, InventorySnapshot inventory) { }
    public record InventorySnapshot(Integer availableQuantity) { }
}
