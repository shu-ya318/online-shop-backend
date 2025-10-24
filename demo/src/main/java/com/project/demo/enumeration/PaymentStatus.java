package com.project.demo.enumeration;

public enum PaymentStatus {
    PAY_ON_DELIVERY, // Waiting for cash on delivery
    AUTHORIZED, // Waiting for external payment authorization to complete
    SUCCESS, 
    FAILED, 
    CANCELLED, 
    REFUNDED
}
