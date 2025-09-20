package com.project.demo.dto.order;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemCreateRequestDTO(
        @NotNull UUID productUuid,
        @NotNull @Min(1) Integer quantity) {
}
