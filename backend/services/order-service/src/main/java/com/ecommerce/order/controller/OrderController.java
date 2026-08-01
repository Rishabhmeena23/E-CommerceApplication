package com.ecommerce.order.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.ecommerce.order.dto.*;
import com.ecommerce.order.security.AuthenticatedUser;
import com.ecommerce.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController @RequestMapping("/orders") @RequiredArgsConstructor
public class OrderController {
    private final OrderService orders;
    @PostMapping public ResponseEntity<OrderResponse> create(@AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orders.create(user.userId(), user.email(), request));
    }
    @GetMapping("/me") public List<OrderResponse> mine(@AuthenticationPrincipal AuthenticatedUser user) { return orders.mine(user.userId()); }
    @GetMapping public List<OrderResponse> all() { return orders.all(); }
    @GetMapping("/{id}") public OrderResponse get(@PathVariable Long id, @AuthenticationPrincipal AuthenticatedUser user) {
        return orders.get(id, user.userId(), user.role());
    }
    @PatchMapping("/{id}/cancel") public OrderResponse cancel(@PathVariable Long id, @AuthenticationPrincipal AuthenticatedUser user) {
        return orders.cancel(id, user.userId());
    }
    @PatchMapping("/{id}/status") public OrderResponse status(@PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) { return orders.updateStatus(id, request); }
}
