package com.orderdemo.order.exception;

/**
 * Custom exception for order validation failures
 */
public class OrderValidationException extends RuntimeException {
    public OrderValidationException(String message) {
        super(message);
    }

    public OrderValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

