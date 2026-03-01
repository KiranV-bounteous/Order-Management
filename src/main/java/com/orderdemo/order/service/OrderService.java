package com.orderdemo.order.service;

import com.orderdemo.order.dao.OrderDAO;
import com.orderdemo.order.dto.CreateOrderRequest;
import com.orderdemo.order.dto.OrderDTO;
import com.orderdemo.order.entity.Order;
import com.orderdemo.order.enums.OrderStatus;
import com.orderdemo.order.exception.OrderNotFoundException;
import com.orderdemo.order.exception.OrderProcessingException;
import com.orderdemo.order.exception.OrderValidationException;
import com.orderdemo.order.util.OrderLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Service for managing orders
 * Implements business logic with dependency injection and concurrent processing
 */
@Service
public class OrderService {

    private final OrderDAO orderDAO;
    private final OrderLogUtil orderLogUtil;
    private final ExecutorService executorService;

    @Autowired
    public OrderService(OrderDAO orderDAO, OrderLogUtil orderLogUtil) {
        this.orderDAO = orderDAO;
        this.orderLogUtil = orderLogUtil;
        this.executorService = Executors.newFixedThreadPool(5); // Thread pool for concurrent processing
    }

    /**
     * Create a new order
     */
    public OrderDTO createOrder(CreateOrderRequest request) {
        validateOrderRequest(request);

        try {
            Order order = Order.createNew(
                    request.getCustomerId(),
                    request.getSymbol(),
                    request.getOrderType(),
                    request.getQuantity(),
                    request.getPrice()
            );

            orderDAO.save(order);
            orderLogUtil.logOrder(order);
            orderLogUtil.logOrderToCSV(order);

            // Process order asynchronously (multithreading)
            processOrderAsync(order.getOrderId());

            return mapToDTO(order);
        } catch (OrderValidationException e) {
            throw e;
        } catch (Exception e) {
            String errorMsg = "Failed to create order: " + e.getMessage();
            orderLogUtil.logError("unknown", errorMsg);
            throw new OrderProcessingException(errorMsg, e);
        }
    }

    /**
     * Get order by ID
     */
    public OrderDTO getOrder(String orderId) {
        Order order = orderDAO.findByIdOrThrow(orderId);
        return mapToDTO(order);
    }

    /**
     * Get all orders
     */
    public List<OrderDTO> getAllOrders() {
        return orderDAO.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get orders by customer
     */
    public List<OrderDTO> getOrdersByCustomer(String customerId) {
        return orderDAO.findByCustomerId(customerId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get orders by status
     */
    public List<OrderDTO> getOrdersByStatus(OrderStatus status) {
        return orderDAO.findByStatus(status).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get orders by symbol
     */
    public List<OrderDTO> getOrdersBySymbol(String symbol) {
        return orderDAO.findBySymbol(symbol).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update order status
     */
    public OrderDTO updateOrderStatus(String orderId, OrderStatus newStatus, String notes) {
        Order order = orderDAO.findByIdOrThrow(orderId);

        try {
            order.updateStatus(newStatus, notes);
            orderDAO.save(order);
            orderLogUtil.logOrderUpdate(order, "STATUS_UPDATE");

            return mapToDTO(order);
        } catch (Exception e) {
            String errorMsg = "Failed to update order status: " + e.getMessage();
            orderLogUtil.logError(orderId, errorMsg);
            throw new OrderProcessingException(errorMsg, e);
        }
    }

    /**
     * Cancel order
     */
    public OrderDTO cancelOrder(String orderId, String reason) {
        Order order = orderDAO.findByIdOrThrow(orderId);

        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new OrderProcessingException("Cannot cancel order with status: " + order.getStatus());
        }

        try {
            order.updateStatus(OrderStatus.CANCELLED, reason);
            orderDAO.save(order);
            orderLogUtil.logOrderUpdate(order, "CANCELLED");

            return mapToDTO(order);
        } catch (Exception e) {
            String errorMsg = "Failed to cancel order: " + e.getMessage();
            orderLogUtil.logError(orderId, errorMsg);
            throw new OrderProcessingException(errorMsg, e);
        }
    }

    /**
     * Delete order
     */
    public void deleteOrder(String orderId) {
        Order order = orderDAO.findByIdOrThrow(orderId);

        try {
            orderDAO.deleteById(orderId);
            orderLogUtil.logOrderUpdate(order, "DELETED");
        } catch (Exception e) {
            String errorMsg = "Failed to delete order: " + e.getMessage();
            orderLogUtil.logError(orderId, errorMsg);
            throw new OrderProcessingException(errorMsg, e);
        }
    }

    /**
     * Validate order request
     */
    private void validateOrderRequest(CreateOrderRequest request) {
        if (request.getCustomerId() == null || request.getCustomerId().trim().isEmpty()) {
            throw new OrderValidationException("Customer ID cannot be null or empty");
        }

        if (request.getSymbol() == null || request.getSymbol().trim().isEmpty()) {
            throw new OrderValidationException("Symbol cannot be null or empty");
        }

        if (request.getOrderType() == null) {
            throw new OrderValidationException("Order type cannot be null");
        }

        if (request.getQuantity() == null || request.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new OrderValidationException("Quantity must be greater than zero");
        }

        if (request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new OrderValidationException("Price must be greater than zero");
        }
    }

    /**
     * Process order asynchronously (Multithreading)
     */
    private void processOrderAsync(String orderId) {
        executorService.submit(() -> {
            try {
                Thread.sleep(1000); // Simulate processing time
                Optional<Order> orderOpt = orderDAO.findById(orderId);

                if (orderOpt.isPresent()) {
                    Order order = orderOpt.get();
                    order.updateStatus(OrderStatus.PROCESSING, "Order processing started");
                    orderDAO.save(order);
                    orderLogUtil.logOrderUpdate(order, "PROCESSING");

                    // Simulate processing with random completion
                    Thread.sleep(2000);

                    if (Math.random() > 0.1) { // 90% success rate
                        order.updateStatus(OrderStatus.COMPLETED, "Order processed successfully");
                        orderLogUtil.logOrderUpdate(order, "COMPLETED");
                    } else {
                        order.updateStatus(OrderStatus.FAILED, "Order processing failed");
                        orderLogUtil.logOrderUpdate(order, "FAILED");
                    }
                    orderDAO.save(order);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Optional<Order> orderOpt = orderDAO.findById(orderId);
                if (orderOpt.isPresent()) {
                    orderLogUtil.logError(orderId, "Order processing interrupted: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Map Order entity to OrderDTO
     */
    private OrderDTO mapToDTO(Order order) {
        return OrderDTO.builder()
                .orderId(order.getOrderId())
                .customerId(order.getCustomerId())
                .symbol(order.getSymbol())
                .orderType(order.getOrderType())
                .quantity(order.getQuantity())
                .price(order.getPrice())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .notes(order.getNotes())
                .build();
    }

    /**
     * Shutdown executor service gracefully
     */
    public void shutdown() {
        executorService.shutdown();
    }
}

