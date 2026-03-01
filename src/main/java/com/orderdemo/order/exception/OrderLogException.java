package com.orderdemo.order.exception;

/**
 * Custom exception for file I/O operations
 */
public class OrderLogException extends RuntimeException {
    public OrderLogException(String message) {
        super(message);
    }

    public OrderLogException(String message, Throwable cause) {
        super(message, cause);
    }
}

