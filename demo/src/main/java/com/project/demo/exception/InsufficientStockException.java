package com.project.demo.exception;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String message) {
        super(message);
    }

    public InsufficientStockException(String productName, Integer availableStock) {
        super(String.format("Not enough stock available, you can only add up to %d %s!", availableStock,
                productName));
    }
}
