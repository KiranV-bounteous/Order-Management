package com.orderdemo.order.config;

import com.orderdemo.order.util.OrderLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Application configuration
 * Initializes beans and resources on startup
 */
@Configuration
public class AppConfig {

    private final OrderLogUtil orderLogUtil;

    @Autowired
    public AppConfig(OrderLogUtil orderLogUtil) {
        this.orderLogUtil = orderLogUtil;
    }

    /**
     * Initialize logs on application startup
     */
    @PostConstruct
    public void initializeLogs() {
        orderLogUtil.initializeLogs();
    }
}

