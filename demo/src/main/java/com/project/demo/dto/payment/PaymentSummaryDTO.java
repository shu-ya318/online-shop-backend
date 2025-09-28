package com.project.demo.dto.payment;

import java.math.BigDecimal;
import java.util.UUID;

import com.project.demo.enumeration.PaymentMethod;
import com.project.demo.enumeration.PaymentStatus;

public record PaymentSummaryDTO(
        UUID uuid,
        PaymentStatus status,
        PaymentMethod method,
        BigDecimal amount,
        String currency) {
}
