package com.project.demo.dto.payment;

import java.math.BigDecimal;
import java.util.UUID;

import com.project.demo.enumeration.PaymentMethod;
import com.project.demo.enumeration.PaymentStatus;

public record PaymentResponseDTO(
        UUID uuid,
        String transactionId,
        PaymentStatus status,
        PaymentMethod method,
        BigDecimal amount,
        String currency,

        // ===== Relation =====
        UUID orderUuid,

        // ===== Gateway Return =====
        String redirectUrl) {
}
