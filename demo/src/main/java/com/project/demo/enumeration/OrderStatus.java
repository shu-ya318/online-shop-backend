package com.project.demo.enumeration;

public enum OrderStatus {
    PENDING, // Created order, not selected payment method or external payment not successful
    PROCESSING, // Selected cash on delivery or external payment successful, enter preparation
    ON_THE_WAY, // Preparation completed, delivery in progress
    COMPLETED, // Pickup completed
    CANCELLED       
}
