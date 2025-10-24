package com.project.demo.mapper;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import com.project.demo.dto.cart.CartItemDTO;
import com.project.demo.dto.cart.CartResponseDTO;
import com.project.demo.model.Cart;
import com.project.demo.model.CartItem;
import com.project.demo.model.Product;
import com.project.demo.mapper.util.PriceCalculationUtils;

@Mapper(componentModel = "spring", uses = { PriceCalculationUtils.class, ProductMapper.class })
public interface CartMapper {
    // --------- Cart ---------

    // 1-1.Field names are different
    @Mapping(target = "cartUuid", source = "uuid")
    // 1-2.Nested object fields, only get object specified fields
    @Mapping(target = "userUuid", source = "user.uuid")
    // 2.Complex object collection conversion
    @Mapping(target = "items", source = "items", qualifiedByName = "toCartItemDTOs")
    // 3.Dynamic calculated fields
    @Mapping(target = "subtotal", source = "items", qualifiedByName = "calculateSubtotal")
    @Mapping(target = "shipping", source = "items", qualifiedByName = "calculateShipping")
    @Mapping(target = "total", source = "items", qualifiedByName = "calculateTotal")
    @Mapping(target = "totalQuantity", source = "items", qualifiedByName = "calculateTotalQuantity")
    CartResponseDTO toCartResponseDTO(Cart cart);

    // --------- CartItem ---------

    @Named("toCartItemDTOs")
    default List<CartItemDTO> toCartItemDTOs(Set<CartItem> items) {
        if (items == null) {
            return Collections.emptyList();
        }

        ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

        List<CartItemDTO> dtoList = items.stream()
                .sorted((item1, item2) -> Long.compare(item1.getId(), item2.getId()))
                .map(item -> toCartItemDTO(item, productMapper))
                .collect(Collectors.toList());

        return dtoList;
    }

    default CartItemDTO toCartItemDTO(CartItem cartItem, ProductMapper productMapper) {
        Product product = cartItem.getProduct();

        if (product == null) {
            return null;
        }

        BigDecimal discountPrice = productMapper.calculateDiscountPrice(product);
        String imageUrl = product.getImageUrl() == null ? "" : product.getImageUrl();
        BigDecimal discountPercentage = product.getDiscountPercentage() == null ? BigDecimal.ZERO
                : product.getDiscountPercentage();

        CartItemDTO cartItemDTO = new CartItemDTO(
                product.getName(),
                product.getPrice(),
                discountPercentage,
                discountPrice,
                imageUrl,
                cartItem.getQuantity(),
                product.getUuid());

        return cartItemDTO;
    }
}
