package com.orderdemo.order.service;

import com.orderdemo.order.dao.OrderDAO;
import com.orderdemo.order.dto.OrderAnalyticsDTO;
import com.orderdemo.order.entity.Order;
import com.orderdemo.order.enums.OrderStatus;
import com.orderdemo.order.enums.OrderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for providing order analytics using Java Streams
 * Demonstrates Streams API for data aggregation
 */
@Service
public class OrderAnalyticsService {

    private final OrderDAO orderDAO;

    @Autowired
    public OrderAnalyticsService(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }

    /**
     * Get comprehensive order analytics
     */
    public OrderAnalyticsDTO getAnalytics() {
        List<Order> allOrders = orderDAO.findAll();

        return OrderAnalyticsDTO.builder()
                .totalOrderAmount(calculateTotalOrderAmount(allOrders))
                .totalBuyOrders(countByOrderType(allOrders, OrderType.BUY))
                .totalSellOrders(countByOrderType(allOrders, OrderType.SELL))
                .topCustomerByVolume(getTopCustomerByVolume(allOrders))
                .topCustomerVolume(getTopCustomerVolume(allOrders))
                .totalOrders((long) allOrders.size())
                .ordersByStatus(groupOrdersByStatus(allOrders))
                .amountByOrderType(groupAmountByOrderType(allOrders))
                .ordersByCustomer(groupOrdersByCustomer(allOrders))
                .build();
    }

    /**
     * Calculate total order amount using Streams
     */
    public BigDecimal calculateTotalOrderAmount(List<Order> orders) {
        return orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Count orders by type using Streams
     */
    public long countByOrderType(List<Order> orders, OrderType orderType) {
        return orders.stream()
                .filter(order -> order.getOrderType() == orderType)
                .count();
    }

    /**
     * Get top customer by volume (total order amount) using Streams
     */
    public String getTopCustomerByVolume(List<Order> orders) {
        return orders.stream()
                .collect(Collectors.groupingBy(
                        Order::getCustomerId,
                        Collectors.reducing(BigDecimal.ZERO, Order::getTotalAmount, BigDecimal::add)
                ))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
    }

    /**
     * Get top customer volume amount
     */
    public BigDecimal getTopCustomerVolume(List<Order> orders) {
        return orders.stream()
                .collect(Collectors.groupingBy(
                        Order::getCustomerId,
                        Collectors.reducing(BigDecimal.ZERO, Order::getTotalAmount, BigDecimal::add)
                ))
                .values().stream()
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Group orders by status using Streams
     */
    public Map<OrderStatus, Long> groupOrdersByStatus(List<Order> orders) {
        return orders.stream()
                .collect(Collectors.groupingBy(
                        Order::getStatus,
                        Collectors.counting()
                ));
    }

    /**
     * Group amount by order type using Streams
     */
    public Map<OrderType, BigDecimal> groupAmountByOrderType(List<Order> orders) {
        return orders.stream()
                .collect(Collectors.groupingBy(
                        Order::getOrderType,
                        Collectors.reducing(BigDecimal.ZERO, Order::getTotalAmount, BigDecimal::add)
                ));
    }

    /**
     * Group orders by customer using Streams
     */
    public Map<String, Long> groupOrdersByCustomer(List<Order> orders) {
        return orders.stream()
                .collect(Collectors.groupingBy(
                        Order::getCustomerId,
                        Collectors.counting()
                ));
    }

    /**
     * Get average order value
     */
    public BigDecimal getAverageOrderValue(List<Order> orders) {
        if (orders.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalAmount = calculateTotalOrderAmount(orders);
        return totalAmount.divide(new BigDecimal(orders.size()), 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Get orders above threshold value
     */
    public List<Order> getOrdersAboveValue(List<Order> orders, BigDecimal threshold) {
        return orders.stream()
                .filter(order -> order.getTotalAmount().compareTo(threshold) >= 0)
                .sorted((o1, o2) -> o2.getTotalAmount().compareTo(o1.getTotalAmount()))
                .collect(Collectors.toList());
    }
}

