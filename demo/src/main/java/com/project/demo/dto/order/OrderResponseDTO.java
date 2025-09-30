package com.project.demo.dto.order;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.project.demo.enumeration.OrderStatus;
import com.project.demo.dto.payment.PaymentSummaryDTO;

public record OrderResponseDTO(
        UUID orderUuid,

        OrderStatus status,
        
        List<OrderItemDTO> items,
        
        BigDecimal subtotal,
        
        BigDecimal shipping,
        
        BigDecimal total,
        
        Integer totalQuantity,

        // ===== 關聯 =====
        UUID userUuid,
        
        PaymentSummaryDTO payment) {
}
