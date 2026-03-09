package com.orderdemo.analytics.entity;

import com.orderdemo.analytics.enums.OrderStatus;
import com.orderdemo.analytics.enums.OrderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Order entity representing a trading order
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    private String orderId;
    private String customerId;
    private String symbol;
    private OrderType orderType;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String notes;

    /**
     * Factory method to create a new order with defaults
     */
    public static Order createNew(String customerId, String symbol, OrderType orderType,
                                  BigDecimal quantity, BigDecimal price) {
        BigDecimal totalAmount = quantity.multiply(price);
        LocalDateTime now = LocalDateTime.now();

        return Order.builder()
                .orderId(UUID.randomUUID().toString())
                .customerId(customerId)
                .symbol(symbol)
                .orderType(orderType)
                .quantity(quantity)
                .price(price)
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Update order status and timestamp
     */
    public void updateStatus(OrderStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Update order status with notes
     */
    public void updateStatus(OrderStatus newStatus, String notes) {
        this.status = newStatus;
        this.notes = notes;
        this.updatedAt = LocalDateTime.now();
    }
}
