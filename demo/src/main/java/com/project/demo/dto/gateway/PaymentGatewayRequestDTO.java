package com.project.demo.dto.gateway;

import jakarta.validation.constraints.NotNull;

public record PaymentGatewayRequestDTO(
    @NotNull String paymentId,
    @NotNull String payerId) {
}
