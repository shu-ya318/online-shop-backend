package com.project.demo.dto.order;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemCreateRequestDTO(
        @NotNull (message = "Quantity is required!")
        @Min(value = 1, message = "Quantity must be at least 1!") 
        Integer quantity,

        // ===== Relation =====
        @Schema(example = "3f4a5b6c-7d8e-9f0a-1b2c-3d4e5f6a7b8c")
        @NotNull(message = "Product uuid is required!") UUID productUuid) {
}
