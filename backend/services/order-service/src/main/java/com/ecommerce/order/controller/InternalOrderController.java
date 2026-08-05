package com.ecommerce.order.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.dto.PaymentUpdateRequest;
import com.ecommerce.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@RestController @RequestMapping("/internal/orders") @RequiredArgsConstructor
public class InternalOrderController {
    private final OrderService orders;
    @Value("${internal.service.key}") private String serviceKey;
    @GetMapping public List<OrderResponse> all(@RequestHeader("X-Internal-Service-Key") String key) { requireKey(key); return orders.all(); }
    @GetMapping("/{id}") public OrderResponse get(@PathVariable Long id,
            @RequestHeader("X-Internal-Service-Key") String key) { requireKey(key); return orders.internalGet(id); }
    @PatchMapping("/{id}/payment") public OrderResponse payment(@PathVariable Long id,
            @RequestHeader("X-Internal-Service-Key") String key, @Valid @RequestBody PaymentUpdateRequest request) {
        requireKey(key); return orders.applyPayment(id, request);
    }
    private void requireKey(String key) {
        if (!serviceKey.equals(key)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid service key");
    }
}
