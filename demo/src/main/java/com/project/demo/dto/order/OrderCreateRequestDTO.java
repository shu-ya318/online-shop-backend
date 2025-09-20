package com.project.demo.dto.order;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record OrderCreateRequestDTO(
        @NotNull String recipientName,
        @NotNull String recipientPhoneNumber,
        @NotNull String recipientAddress,
        @NotNull @NotEmpty(message = "Order items cannot be empty!") @Valid List<OrderItemCreateRequestDTO> items,
        String orderNotes
        ) {
}
