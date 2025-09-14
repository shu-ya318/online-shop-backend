package com.project.demo.dto.cart;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CartResponseDTO(
	UUID cartUuid,
    UUID userUuid,
    List<CartItemDTO> items, // 有序性、易用性(便於客戶端把List 序列化成 JSON 陣列)
    BigDecimal total,
    BigDecimal discountTotal,
    Integer totalQuantity
) {
}
