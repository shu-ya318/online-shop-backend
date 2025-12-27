package com.project.demo.dto.gateway;

import jakarta.validation.constraints.NotNull;

public record PaymentGatewayRequestDTO(
    @NotNull (message = "Payment id is required!")
    String paymentId,

    @NotNull (message = "Payer id is required!")
    String payerId) {
}
