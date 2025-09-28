package com.project.demo.dto.payment;

import java.util.UUID;

import com.project.demo.enumeration.PaymentMethod;

import jakarta.validation.constraints.NotNull;

public record PaymentRequestDTO(
        @NotNull PaymentMethod method,
        
        // ===== 關聯 =====
        @NotNull UUID orderUuid) {
}
