package com.project.demo.dto.order;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemCreateRequestDTO(
        @NotNull 
        @Min(1) 
        Integer quantity,
        
        // ===== 關聯 =====
        @NotNull 
        UUID productUuid,

        @NotNull 
        UUID userUuid) {
}
