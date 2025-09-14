package com.project.demo.dto.cart;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemDTO(
        UUID productUuid,
        String productName,
        BigDecimal price,
        BigDecimal discountPercentage,
        BigDecimal discountPrice,
        String imageUrl,
        Integer quantity) {
}
