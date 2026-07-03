package com.training.orderdemo.service;

import java.time.LocalDateTime;

public class Order {

    private final Long orderId;
    private final OrderStatus status;
    private final LocalDateTime createdAt;

    public Order(Long orderId, OrderStatus status, LocalDateTime createdAt) {
        this.orderId = orderId;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getOrderId() {
        return orderId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
