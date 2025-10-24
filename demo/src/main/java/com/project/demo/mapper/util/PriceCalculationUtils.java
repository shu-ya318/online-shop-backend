package com.project.demo.mapper.util;

import com.project.demo.mapper.ProductMapper;
import com.project.demo.model.Sellable;

import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

public class PriceCalculationUtils {

    private static final BigDecimal SHIPPING_FEE = new BigDecimal("60");

    @Named("calculateSubtotal")
    public static BigDecimal calculateSubtotal(Set<? extends Sellable> items) {
        if (items == null || items.isEmpty()) {
            BigDecimal zero = BigDecimal.ZERO.setScale(0, RoundingMode.HALF_UP);
            return zero;
        }

        ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

        BigDecimal subtotal = items.stream()
                .map(item -> {
                    BigDecimal discountPrice = productMapper.calculateDiscountPrice(item.getProduct());
                    return discountPrice.multiply(new BigDecimal(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal result = subtotal.setScale(0, RoundingMode.HALF_UP);
        return result;
    }

    @Named("calculateShipping")
    public static BigDecimal calculateShipping(Set<? extends Sellable> items) {
        BigDecimal subtotal = calculateSubtotal(items);
        BigDecimal shippingFee;

        if (subtotal.compareTo(new BigDecimal("300")) >= 0) {
            shippingFee = BigDecimal.ZERO.setScale(0, RoundingMode.HALF_UP);
        } else {
            shippingFee = SHIPPING_FEE;
        }
        return shippingFee;
    }

    @Named("calculateTotal")
    public static BigDecimal calculateTotal(Set<? extends Sellable> items) {
        BigDecimal subtotal = calculateSubtotal(items);
        BigDecimal shipping = calculateShipping(items);

        BigDecimal total = subtotal.add(shipping);
        BigDecimal result = total.setScale(0, RoundingMode.HALF_UP);
        return result;
    }

    @Named("calculateTotalQuantity")
    public static Integer calculateTotalQuantity(Set<? extends Sellable> items) {
        Integer totalQuantity = 0;
        if (items != null) {
            totalQuantity = items.stream()
                    .mapToInt(Sellable::getQuantity)
                    .sum();
        }
        return totalQuantity;
    }
}
