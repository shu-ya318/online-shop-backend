package com.project.demo.dto.payment;

import java.util.UUID;

import com.project.demo.enumeration.PaymentStatus;

public record PaymentCaptureResponseDTO(
        PaymentStatus status,

        // ===== Relation =====
        UUID orderUuid) {
}
