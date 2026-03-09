package com.orderdemo.analytics.dto;

import com.orderdemo.analytics.enums.OrderStatus;
import com.orderdemo.analytics.enums.OrderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for order analytics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderAnalyticsDTO {
    private BigDecimal totalOrderAmount;
    private Long totalBuyOrders;
    private Long totalSellOrders;
    private String topCustomerByVolume;
    private BigDecimal topCustomerVolume;
    private Long totalOrders;
    private java.util.Map<OrderStatus, Long> ordersByStatus;
    private java.util.Map<OrderType, BigDecimal> amountByOrderType;
    private java.util.Map<String, Long> ordersByCustomer;
}
