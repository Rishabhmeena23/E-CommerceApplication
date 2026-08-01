package com.ecommerce.admin_service.client.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.ecommerce.admin_service.client.OrderServiceClient;
import com.ecommerce.admin_service.dto.OrderDto;

@Service
public class HttpOrderServiceClient implements OrderServiceClient {
    private final RestClient client;
    private final String serviceKey;
    public HttpOrderServiceClient(@Value("${services.order.url:http://localhost:8088}") String url,
            @Value("${internal.service.key}") String serviceKey) {
        this.client = RestClient.builder().baseUrl(url).build(); this.serviceKey = serviceKey;
    }
    @Override public List<OrderDto> getAllOrders() {
        return client.get().uri("/internal/orders").header("X-Internal-Service-Key", serviceKey)
                .retrieve().body(new ParameterizedTypeReference<List<OrderDto>>() {});
    }
}
