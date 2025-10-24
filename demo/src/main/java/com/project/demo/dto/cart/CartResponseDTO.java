package com.project.demo.dto.cart;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CartResponseDTO(
        UUID cartUuid,
        List<CartItemDTO> items, // Orderness and ease of use
        BigDecimal subtotal,
        BigDecimal shipping,
        BigDecimal total,
        Integer totalQuantity,

        // ===== Relation =====
        UUID userUuid) {
}
