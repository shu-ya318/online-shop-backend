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
            return BigDecimal.ZERO.setScale(0, RoundingMode.HALF_UP);
        }

        ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

        return items.stream()
                .map(item -> {
                    BigDecimal discountPrice = productMapper.calculateDiscountPrice(item.getProduct());
                    return discountPrice.multiply(new BigDecimal(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(0, RoundingMode.HALF_UP);
    }

    @Named("calculateShipping")
    public static BigDecimal calculateShipping(Set<? extends Sellable> items) {
        BigDecimal subtotal = calculateSubtotal(items);

        if (subtotal.compareTo(new BigDecimal("300")) >= 0) {
            return BigDecimal.ZERO.setScale(0, RoundingMode.HALF_UP);
        } else {
            return SHIPPING_FEE;
        }
    }

    @Named("calculateTotal")
    public static BigDecimal calculateTotal(Set<? extends Sellable> items) {
        BigDecimal subtotal = calculateSubtotal(items);
        BigDecimal shipping = calculateShipping(items);

        return subtotal.add(shipping).setScale(0, RoundingMode.HALF_UP);
    }

    @Named("calculateTotalQuantity")
    public static Integer calculateTotalQuantity(Set<? extends Sellable> items) {
        if (items == null) {
            return 0;
        }

        return items.stream()
                .mapToInt(Sellable::getQuantity)
                .sum();
    }
}
