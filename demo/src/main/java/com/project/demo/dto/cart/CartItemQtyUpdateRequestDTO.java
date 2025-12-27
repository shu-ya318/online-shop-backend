package com.project.demo.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartItemQtyUpdateRequestDTO(
        @NotNull(message = "Quantity is required!") 
        @Min(value = 1, message = "Quantity must be at least 1!") 
        Integer quantity) {
}
