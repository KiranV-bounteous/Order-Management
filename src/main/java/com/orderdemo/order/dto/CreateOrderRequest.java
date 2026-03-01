package com.orderdemo.order.dto;

import com.orderdemo.order.enums.OrderStatus;
import com.orderdemo.order.enums.OrderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for creating new orders
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    private String customerId;
    private String symbol;
    private OrderType orderType;
    private BigDecimal quantity;
    private BigDecimal price;
}

