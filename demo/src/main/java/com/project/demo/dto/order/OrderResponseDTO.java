package com.project.demo.dto.order;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.project.demo.enumeration.OrderStatus;

public record OrderResponseDTO(
        UUID orderUuid,
        UUID userUuid,
        OrderStatus status,
        List<OrderItemDTO> items,
        BigDecimal subtotal,
        BigDecimal shipping,
        BigDecimal total,
        Integer totalQuantity) {
}
