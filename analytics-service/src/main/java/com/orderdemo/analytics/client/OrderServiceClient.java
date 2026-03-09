package com.orderdemo.analytics.client;

import com.orderdemo.analytics.entity.Order;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "order-service")
public interface OrderServiceClient {

    @GetMapping("/orders/internal/all")
    List<Order> getAllOrders();
}
