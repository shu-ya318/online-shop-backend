package com.project.demo.dto.cart;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartAddItemRequestDTO(
        @NotNull @Min(1) Integer quantity,

        // ===== Relation =====
        @NotNull UUID productUuid) {
}
