package com.project.demo.dto.gateway;

import com.project.demo.enumeration.PaymentStatus;

public record PaymentGatewayResponseDTO(
    String transactionId,
    PaymentStatus status,
    String redirectUrl) {
}
