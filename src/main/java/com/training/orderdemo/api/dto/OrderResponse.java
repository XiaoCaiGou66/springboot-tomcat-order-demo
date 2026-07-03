package com.training.orderdemo.api.dto;

import com.training.orderdemo.service.Order;

public class OrderResponse {

    private Long orderId;
    private String status;
    private String message;

    public static OrderResponse from(Order order) {
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getOrderId());
        response.setStatus(order.getStatus().name());
        response.setMessage("order accepted by demo service");
        return response;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
