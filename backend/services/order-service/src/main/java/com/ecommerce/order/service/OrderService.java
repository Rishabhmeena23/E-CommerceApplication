package com.ecommerce.order.service;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.ecommerce.order.client.ProductClient;
import com.ecommerce.order.dto.*;
import com.ecommerce.order.entity.*;
import com.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

@Service @RequiredArgsConstructor
public class OrderService {
    private final OrderRepository repository;
    private final ProductClient products;

    @Transactional
    public OrderResponse create(Long userId, String email, CreateOrderRequest request) {
        CustomerOrder order = new CustomerOrder();
        order.setUserId(userId); order.setCustomerEmail(email); order.setShippingAddress(request.shippingAddress().trim());
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest requested : request.items()) {
            ProductClient.ProductSnapshot product = products.get(requested.productId());
            if (product == null || product.price() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid product");
            if (product.inventory() != null && product.inventory().availableQuantity() != null
                    && product.inventory().availableQuantity() < requested.quantity()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, product.name() + " does not have enough stock");
            }
            OrderItem item = new OrderItem();
            item.setProductId(product.id()); item.setProductName(product.name()); item.setUnitPrice(product.price());
            item.setQuantity(requested.quantity()); item.setSubtotal(product.price().multiply(BigDecimal.valueOf(requested.quantity())));
            order.addItem(item); total = total.add(item.getSubtotal());
        }
        order.setTotalAmount(total);
        return response(repository.save(order));
    }

    @Transactional(readOnly = true) public List<OrderResponse> mine(Long userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::response).toList();
    }
    @Transactional(readOnly = true) public List<OrderResponse> all() {
        return repository.findAllByOrderByCreatedAtDesc().stream().map(this::response).toList();
    }
    @Transactional(readOnly = true) public OrderResponse get(Long id, Long userId, String role) {
        CustomerOrder order = find(id); requireOwnerOrAdmin(order, userId, role); return response(order);
    }
    @Transactional(readOnly = true) public OrderResponse internalGet(Long id) { return response(find(id)); }

    @Transactional public OrderResponse cancel(Long id, Long userId) {
        CustomerOrder order = find(id);
        if (!order.getUserId().equals(userId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This order belongs to another user");
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT && order.getStatus() != OrderStatus.PAYMENT_FAILED)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only unpaid orders can be cancelled");
        order.setStatus(OrderStatus.CANCELLED); return response(repository.save(order));
    }
    @Transactional public OrderResponse updateStatus(Long id, UpdateOrderStatusRequest request) {
        CustomerOrder order = find(id);
        try { order.setStatus(OrderStatus.valueOf(request.status().toUpperCase())); }
        catch (IllegalArgumentException exception) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid order status"); }
        return response(repository.save(order));
    }
    @Transactional public OrderResponse applyPayment(Long id, PaymentUpdateRequest request) {
        CustomerOrder order = find(id);
        if (order.getStatus() == OrderStatus.CANCELLED) throw new ResponseStatusException(HttpStatus.CONFLICT, "Cancelled orders cannot be paid");
        order.setPaymentReference(request.paymentReference());
        order.setStatus("SUCCESS".equalsIgnoreCase(request.paymentStatus()) ? OrderStatus.PAID : OrderStatus.PAYMENT_FAILED);
        return response(repository.save(order));
    }
    private CustomerOrder find(Long id) { return repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found")); }
    private void requireOwnerOrAdmin(CustomerOrder order, Long userId, String role) {
        if (!order.getUserId().equals(userId) && !"ADMIN".equals(role)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This order belongs to another user");
    }
    private OrderResponse response(CustomerOrder order) {
        List<OrderItemResponse> items = order.getItems().stream().map(item -> new OrderItemResponse(item.getId(), item.getProductId(),
                item.getProductName(), item.getUnitPrice(), item.getQuantity(), item.getSubtotal())).toList();
        return new OrderResponse(order.getId(), order.getUserId(), order.getCustomerEmail(), order.getStatus().name(),
                order.getTotalAmount(), order.getShippingAddress(), order.getPaymentReference(), items, order.getCreatedAt(), order.getUpdatedAt());
    }
}
