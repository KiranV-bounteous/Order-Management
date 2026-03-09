package com.orderdemo.analytics.controller;

import com.orderdemo.analytics.dto.ApiResponse;
import com.orderdemo.analytics.dto.OrderAnalyticsDTO;
import com.orderdemo.analytics.service.OrderAnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for Order Analytics
 * Provides endpoints for analytics using Streams API
 */
@RestController
@RequestMapping("/analytics")
public class OrderAnalyticsController {

    private final OrderAnalyticsService orderAnalyticsService;

    @Autowired
    public OrderAnalyticsController(OrderAnalyticsService orderAnalyticsService) {
        this.orderAnalyticsService = orderAnalyticsService;
    }

    /**
     * Get comprehensive order analytics
     * GET /analytics
     */
    @GetMapping
    public ResponseEntity<ApiResponse<OrderAnalyticsDTO>> getAnalytics() {
        try {
            OrderAnalyticsDTO analytics = orderAnalyticsService.getAnalytics();
            return ResponseEntity.ok(ApiResponse.success(analytics, "Analytics retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve analytics: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}
