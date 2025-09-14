package com.project.demo.mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.project.demo.dto.cart.CartItemDTO;
import com.project.demo.dto.cart.CartResponseDTO;
import com.project.demo.model.Cart;
import com.project.demo.model.CartItem;
import com.project.demo.model.Product;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { ProductMapper.class })
public interface CartMapper {

    // --------- Cart ---------
    // 1-1.欄位對應名稱不同
    @Mapping(target = "cartUuid", source = "uuid")
    // 1-2.巢狀物件的欄位，只需要取得物件指定欄位
    @Mapping(target = "userUuid", source = "user.uuid")
    // 2.複雜的物件集合轉換
    @Mapping(target = "items", source = "items", qualifiedByName = "toCartItemDTOs")
    // 3.動態計算的欄位
    @Mapping(target = "total", source = "items", qualifiedByName = "calculateTotal")
    @Mapping(target = "discountTotal", source = "items", qualifiedByName = "calculateDiscountTotal")
    @Mapping(target = "totalQuantity", source = "items", qualifiedByName = "calculateTotalQuantity")
    CartResponseDTO toCartResponseDTO(Cart cart);

    // --------- CartItem ---------
    @Named("toCartItemDTOs")
    default List<CartItemDTO> toCartItemDTOs(Set<CartItem> items) {
        if (items == null)
            return Collections.emptyList();

        ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

        return items.stream()
                .sorted((item1, item2) -> Long.compare(item1.getId(), item2.getId()))
                .map(item -> toCartItemDTO(item, productMapper))
                .collect(Collectors.toList());
    }

    default CartItemDTO toCartItemDTO(CartItem cartItem, ProductMapper productMapper) {
        Product product = cartItem.getProduct();

        if (product == null)
            return null;

        BigDecimal discountPrice = productMapper.calculateDiscountPrice(product);

        return new CartItemDTO(
                product.getUuid(),
                product.getName(),
                product.getPrice(),
                product.getDiscountPercentage(),
                discountPrice,
                product.getImageUrl(),
                cartItem.getQuantity());
    }

    // Utils
    @Named("calculateTotal")
    default BigDecimal calculateTotal(Set<CartItem> items) {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return items.stream()
                .map(item -> item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Named("calculateDiscountTotal")
    default BigDecimal calculateDiscountTotal(Set<CartItem> items) {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

        return items.stream()
                .map(item -> {
                    BigDecimal discountPrice = productMapper.calculateDiscountPrice(item.getProduct());
                    return discountPrice.multiply(new BigDecimal(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Named("calculateTotalQuantity")
    default Integer calculateTotalQuantity(Set<CartItem> items) {
        if (items == null) {
            return 0;
        }

        return items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
}
