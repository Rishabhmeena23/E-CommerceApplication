package com.ecommerce.payment.client;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Component
public class OrderClient {
    private final RestClient client; private final String serviceKey;
    public OrderClient(@Value("${services.order.url}") String url, @Value("${internal.service.key}") String key) {
        this.client = RestClient.builder().baseUrl(url).build(); this.serviceKey = key;
    }
    public OrderSnapshot get(Long id) { try {
        return client.get().uri("/internal/orders/{id}", id).header("X-Internal-Service-Key", serviceKey)
                .retrieve().body(OrderSnapshot.class);
    } catch (Exception exception) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order could not be verified"); } }
    public void updatePayment(Long id, String reference, String status) {
        client.patch().uri("/internal/orders/{id}/payment", id).header("X-Internal-Service-Key", serviceKey)
                .body(new PaymentUpdate(reference, status)).retrieve().toBodilessEntity();
    }
    public record OrderSnapshot(Long id, Long userId, String orderStatus, BigDecimal totalAmount) { }
    private record PaymentUpdate(String paymentReference, String paymentStatus) { }
}
