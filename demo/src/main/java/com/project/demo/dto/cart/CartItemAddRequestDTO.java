package com.project.demo.dto.cart;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartItemAddRequestDTO(
        @NotNull(message = "Quantity is required!") 
        @Min(value = 1, message = "Quantity must be at least 1!") 
        Integer quantity,

        // ===== Relation =====
        @NotNull(message = "Product uuid is required!") 
        UUID productUuid) {
}
