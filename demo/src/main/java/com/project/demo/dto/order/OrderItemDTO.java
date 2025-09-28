package com.project.demo.dto.order;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemDTO(
        String productName,
        BigDecimal unitPrice,
        BigDecimal discountPercentage,
        BigDecimal discountPrice,
        String imageUrl,
        Integer quantity,

        // ===== 關聯 =====
        UUID productUuid) {
}
