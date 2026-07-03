package com.training.orderdemo.service;

import com.training.orderdemo.api.dto.CreateOrderRequest;
import com.training.orderdemo.api.dto.OrderResponse;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderResponse createOrder(CreateOrderRequest request) {
        Order order = new Order(request.getOrderId(), OrderStatus.CREATED, LocalDateTime.now());
        return OrderResponse.from(orderRepository.save(order));
    }

    public OrderResponse getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("order not found: " + orderId));
        return OrderResponse.from(order);
    }
}
