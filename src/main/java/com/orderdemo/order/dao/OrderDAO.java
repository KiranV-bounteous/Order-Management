package com.orderdemo.order.dao;

import com.orderdemo.order.entity.Order;
import com.orderdemo.order.enums.OrderStatus;
import com.orderdemo.order.exception.OrderNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * DAO for managing orders in-memory using collections
 * Thread-safe implementation using ConcurrentHashMap
 */
@Repository
public class OrderDAO {
    private final Map<String, Order> orders = new ConcurrentHashMap<>();

    /**
     * Save or update an order
     */
    public void save(Order order) {
        if (order == null || order.getOrderId() == null) {
            throw new IllegalArgumentException("Order and orderId cannot be null");
        }
        orders.put(order.getOrderId(), order);
    }

    /**
     * Find order by ID
     */
    public Optional<Order> findById(String orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }

    /**
     * Find order by ID or throw exception
     */
    public Order findByIdOrThrow(String orderId) {
        return findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));
    }

    /**
     * Find all orders
     */
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }

    /**
     * Find orders by customer ID
     */
    public List<Order> findByCustomerId(String customerId) {
        return orders.values().stream()
                .filter(order -> order.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    /**
     * Find orders by status
     */
    public List<Order> findByStatus(OrderStatus status) {
        return orders.values().stream()
                .filter(order -> order.getStatus() == status)
                .collect(Collectors.toList());
    }

    /**
     * Find orders by symbol
     */
    public List<Order> findBySymbol(String symbol) {
        return orders.values().stream()
                .filter(order -> order.getSymbol().equals(symbol))
                .collect(Collectors.toList());
    }

    /**
     * Delete order by ID
     */
    public void deleteById(String orderId) {
        orders.remove(orderId);
    }

    /**
     * Check if order exists
     */
    public boolean existsById(String orderId) {
        return orders.containsKey(orderId);
    }

    /**
     * Get total count of orders
     */
    public long count() {
        return orders.size();
    }

    /**
     * Clear all orders (use with caution)
     */
    public void deleteAll() {
        orders.clear();
    }
}

