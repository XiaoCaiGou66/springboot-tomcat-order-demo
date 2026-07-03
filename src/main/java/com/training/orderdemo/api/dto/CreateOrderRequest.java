package com.training.orderdemo.api.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class CreateOrderRequest {

    @NotNull(message = "orderId must not be null")
    @Positive(message = "orderId must be positive")
    private Long orderId;

    private String userId;

    private Integer amount;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
