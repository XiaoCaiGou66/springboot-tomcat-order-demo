package com.training.orderdemo.api;

import com.training.orderdemo.api.dto.ApiResponse;
import com.training.orderdemo.api.dto.CreateOrderRequest;
import com.training.orderdemo.api.dto.OrderResponse;
import com.training.orderdemo.service.OrderService;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public ApiResponse<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ApiResponse.success(response);
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse> detail(@PathVariable Long orderId) {
        return ApiResponse.success(orderService.getOrder(orderId));
    }
}
