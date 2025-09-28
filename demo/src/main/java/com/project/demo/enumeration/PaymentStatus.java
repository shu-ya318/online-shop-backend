package com.project.demo.enumeration;

public enum PaymentStatus {
    PAY_ON_DELIVERY, // 等待貨到付款
    AUTHORIZED, // 等待在第三方支付完成授權
    SUCCESS, 
    FAILED, 
    CANCELLED, 
    REFUNDED
}
