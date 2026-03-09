package com.orderdemo.order;

import com.orderdemo.order.dao.OrderDAO;
import com.orderdemo.order.dto.CreateOrderRequest;
import com.orderdemo.order.dto.OrderDTO;
import com.orderdemo.order.enums.OrderStatus;
import com.orderdemo.order.enums.OrderType;
import com.orderdemo.order.service.OrderService;
import com.orderdemo.order.service.OrderAnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Order Management System
 */
@SpringBootTest
class OrderApplicationTests {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderAnalyticsService analyticsService;

    @Autowired
    private OrderDAO orderDAO;

    @BeforeEach
    void setUp() {
        orderDAO.deleteAll();
    }

    @Test
    void contextLoads() {
        assertNotNull(orderService);
        assertNotNull(analyticsService);
        assertNotNull(orderDAO);
    }

    @Test
    void testCreateOrder() {
        CreateOrderRequest request = CreateOrderRequest.builder()
                .customerId("CUST001")
                .symbol("APPL")
                .orderType(OrderType.BUY)
                .quantity(new BigDecimal("10"))
                .price(new BigDecimal("150.00"))
                .build();

        OrderDTO order = orderService.createOrder(request);

        assertNotNull(order.getOrderId());
        assertEquals("CUST001", order.getCustomerId());
        assertEquals("APPL", order.getSymbol());
        assertEquals(OrderType.BUY, order.getOrderType());
        assertEquals(new BigDecimal("1500.00"), order.getTotalAmount());
        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    void testGetOrder() {
        CreateOrderRequest request = CreateOrderRequest.builder()
                .customerId("CUST001")
                .symbol("APPL")
                .orderType(OrderType.BUY)
                .quantity(new BigDecimal("10"))
                .price(new BigDecimal("150.00"))
                .build();

        OrderDTO createdOrder = orderService.createOrder(request);
        OrderDTO retrievedOrder = orderService.getOrder(createdOrder.getOrderId());

        assertEquals(createdOrder.getOrderId(), retrievedOrder.getOrderId());
        assertEquals(createdOrder.getCustomerId(), retrievedOrder.getCustomerId());
    }

    @Test
    void testGetAllOrders() {
        CreateOrderRequest request1 = CreateOrderRequest.builder()
                .customerId("CUST001")
                .symbol("APPL")
                .orderType(OrderType.BUY)
                .quantity(new BigDecimal("10"))
                .price(new BigDecimal("150.00"))
                .build();

        CreateOrderRequest request2 = CreateOrderRequest.builder()
                .customerId("CUST002")
                .symbol("GOOG")
                .orderType(OrderType.SELL)
                .quantity(new BigDecimal("5"))
                .price(new BigDecimal("2800.00"))
                .build();

        orderService.createOrder(request1);
        orderService.createOrder(request2);

        List<OrderDTO> orders = orderService.getAllOrders();

        assertEquals(2, orders.size());
    }

    @Test
    void testGetOrdersByCustomer() {
        CreateOrderRequest request1 = CreateOrderRequest.builder()
                .customerId("CUST001")
                .symbol("APPL")
                .orderType(OrderType.BUY)
                .quantity(new BigDecimal("10"))
                .price(new BigDecimal("150.00"))
                .build();

        CreateOrderRequest request2 = CreateOrderRequest.builder()
                .customerId("CUST001")
                .symbol("MSFT")
                .orderType(OrderType.BUY)
                .quantity(new BigDecimal("20"))
                .price(new BigDecimal("300.00"))
                .build();

        orderService.createOrder(request1);
        orderService.createOrder(request2);

        List<OrderDTO> orders = orderService.getOrdersByCustomer("CUST001");

        assertEquals(2, orders.size());
    }

    @Test
    void testUpdateOrderStatus() {
        CreateOrderRequest request = CreateOrderRequest.builder()
                .customerId("CUST001")
                .symbol("APPL")
                .orderType(OrderType.BUY)
                .quantity(new BigDecimal("10"))
                .price(new BigDecimal("150.00"))
                .build();

        OrderDTO createdOrder = orderService.createOrder(request);
        OrderDTO updatedOrder = orderService.updateOrderStatus(createdOrder.getOrderId(), OrderStatus.PROCESSING, "Processing");

        assertEquals(OrderStatus.PROCESSING, updatedOrder.getStatus());
        assertEquals("Processing", updatedOrder.getNotes());
    }

    @Test
    void testCancelOrder() {
        CreateOrderRequest request = CreateOrderRequest.builder()
                .customerId("CUST001")
                .symbol("APPL")
                .orderType(OrderType.BUY)
                .quantity(new BigDecimal("10"))
                .price(new BigDecimal("150.00"))
                .build();

        OrderDTO createdOrder = orderService.createOrder(request);
        OrderDTO cancelledOrder = orderService.cancelOrder(createdOrder.getOrderId(), "User requested cancellation");

        assertEquals(OrderStatus.CANCELLED, cancelledOrder.getStatus());
    }

    @Test
    void testDeleteOrder() {
        CreateOrderRequest request = CreateOrderRequest.builder()
                .customerId("CUST001")
                .symbol("APPL")
                .orderType(OrderType.BUY)
                .quantity(new BigDecimal("10"))
                .price(new BigDecimal("150.00"))
                .build();

        OrderDTO createdOrder = orderService.createOrder(request);
        orderService.deleteOrder(createdOrder.getOrderId());

        List<OrderDTO> orders = orderService.getAllOrders();
        assertEquals(0, orders.size());
    }

    @Test
    void testAnalyticsCalculations() {
        CreateOrderRequest request1 = CreateOrderRequest.builder()
                .customerId("CUST001")
                .symbol("APPL")
                .orderType(OrderType.BUY)
                .quantity(new BigDecimal("10"))
                .price(new BigDecimal("150.00"))
                .build();

        CreateOrderRequest request2 = CreateOrderRequest.builder()
                .customerId("CUST001")
                .symbol("GOOG")
                .orderType(OrderType.SELL)
                .quantity(new BigDecimal("5"))
                .price(new BigDecimal("2800.00"))
                .build();

        orderService.createOrder(request1);
        orderService.createOrder(request2);

        var analytics = analyticsService.getAnalytics();

        assertEquals(2, analytics.getTotalOrders());
        assertEquals(1, analytics.getTotalBuyOrders());
        assertEquals(1, analytics.getTotalSellOrders());
        assertEquals("CUST001", analytics.getTopCustomerByVolume());
    }

    @Test
    void testInvalidOrderValidation() {
        CreateOrderRequest request = CreateOrderRequest.builder()
                .customerId("")
                .symbol("APPL")
                .orderType(OrderType.BUY)
                .quantity(new BigDecimal("10"))
                .price(new BigDecimal("150.00"))
                .build();

        assertThrows(RuntimeException.class, () -> orderService.createOrder(request));
    }
}
