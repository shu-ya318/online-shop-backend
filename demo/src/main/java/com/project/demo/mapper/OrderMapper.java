package com.project.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.project.demo.mapper.util.PriceCalculationUtils;
import com.project.demo.dto.order.OrderItemDTO;
import com.project.demo.dto.order.OrderResponseDTO;
import com.project.demo.model.Order;
import com.project.demo.model.OrderItem;
import com.project.demo.model.Product;
import com.project.demo.model.Payment;
import com.project.demo.dto.payment.PaymentSummaryDTO;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { PaymentMapper.class,
		PriceCalculationUtils.class })
public interface OrderMapper {
    // --------- Order ---------

    @Mapping(target = "orderUuid", source = "uuid")
    @Mapping(target = "userUuid", source = "user.uuid")
    @Mapping(target = "total", source = "total")
    @Mapping(target = "items", source = "items", qualifiedByName = "toOrderItemDTOs")
    @Mapping(target = "subtotal", source = "items", qualifiedByName = "calculateSubtotal")
    @Mapping(target = "shipping", source = "items", qualifiedByName = "calculateShipping")
    @Mapping(target = "totalQuantity", source = "items", qualifiedByName = "calculateTotalQuantity")
    @Mapping(target = "payment", source = "payments", qualifiedByName = "toLatestPaymentSummaryDTO")
    OrderResponseDTO toOrderResponseDTO(Order order);

    List<OrderResponseDTO> toOrderResponseDTOs(List<Order> orders);

    @Named("toLatestPaymentSummaryDTO")
    default PaymentSummaryDTO toLatestPaymentSummaryDTO(List<Payment> payments) {
        if (payments == null || payments.isEmpty()) {
            return null;
        }

        PaymentMapper paymentMapper = Mappers.getMapper(PaymentMapper.class);

        PaymentSummaryDTO latestPayment = payments.stream()
                .max(Comparator.comparing(Payment::getCreatedAt))
                .map(paymentMapper::toPaymentSummaryDTO)
                .orElse(null);

        return latestPayment;
    }

    // --------- OrderItem ---------

    @Named("toOrderItemDTOs")
    default List<OrderItemDTO> toOrderItemDTOs(Set<OrderItem> items) {
        if (items == null) {
            return Collections.emptyList();
        }

        ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

        List<OrderItemDTO> dtoList = items.stream()
                .sorted((item1, item2) -> Long.compare(item1.getId(), item2.getId()))
                .map(item -> toOrderItemDTO(item, productMapper))
                .collect(Collectors.toList());

        return dtoList;
    }

    default OrderItemDTO toOrderItemDTO(OrderItem orderItem, ProductMapper productMapper) {
        Product product = orderItem.getProduct();

        if (product == null) {
            return null;
        }

        BigDecimal discountPrice = productMapper.calculateDiscountPrice(product);
        String imageUrl = product.getImageUrl() == null ? "" : product.getImageUrl();
        BigDecimal discountPercentage = product.getDiscountPercentage() == null ? BigDecimal.ZERO
                : product.getDiscountPercentage();

        OrderItemDTO orderItemDTO = new OrderItemDTO(
                product.getName(),
                product.getPrice(),
                discountPercentage,
                discountPrice,
                imageUrl,
                orderItem.getQuantity(),
                product.getUuid());

        return orderItemDTO;
    }
}
