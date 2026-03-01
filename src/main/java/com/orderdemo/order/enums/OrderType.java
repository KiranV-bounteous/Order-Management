package com.orderdemo.order.enums;

/**
 * Enum for order types - BUY or SELL
 */
public enum OrderType {
    BUY("Buy Order"),
    SELL("Sell Order");

    private final String description;

    OrderType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

