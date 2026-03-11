package com.project.demo.dto.payment;

import java.util.UUID;

import com.project.demo.enumeration.PaymentMethod;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record PaymentRequestDTO(
        @NotNull(message = "Payment method is required!")
        PaymentMethod method,
        
        // ===== Relation =====
        @Schema(type = "string", example = "string")
        @NotNull(message = "Order uuid is required!")
        UUID orderUuid) {
}
