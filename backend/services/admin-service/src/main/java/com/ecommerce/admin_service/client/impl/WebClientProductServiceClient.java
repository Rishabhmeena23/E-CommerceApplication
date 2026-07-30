package com.ecommerce.admin_service.client.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.ecommerce.admin_service.client.ProductServiceClient;
import com.ecommerce.admin_service.dto.ProductDto;

@Service
public class WebClientProductServiceClient implements ProductServiceClient {
    private final WebClient webClient;

    public WebClientProductServiceClient(WebClient.Builder builder,
            @Value("${services.product.url:http://localhost:8085}") String productUrl) {
        this.webClient = builder.baseUrl(productUrl).build();
    }

    @Override
    public List<ProductDto> getAllProducts() {
        List<ProductSummary> products = webClient.get().uri("/products").retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ProductSummary>>() { }).block();
        return products == null ? List.of() : products.stream()
                .map(product -> new ProductDto(product.id(), product.name(),
                        product.description(), product.categoryName(), product.price(), "ACTIVE"))
                .toList();
    }

    private record ProductSummary(Long id, String name, String description,
            String categoryName, BigDecimal price) { }
}
