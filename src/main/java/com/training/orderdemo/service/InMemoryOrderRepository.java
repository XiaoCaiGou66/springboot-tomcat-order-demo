package com.training.orderdemo.service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryOrderRepository implements OrderRepository {

    private final Map<Long, Order> orders = new ConcurrentHashMap<>();

    @Override
    public Order save(Order order) {
        orders.put(order.getOrderId(), order);
        return order;
    }

    @Override
    public Optional<Order> findById(Long orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }
}
