package com.project.demo.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartUpdateItemRequestDTO(
        @NotNull @Min(1) Integer quantity) {
}
