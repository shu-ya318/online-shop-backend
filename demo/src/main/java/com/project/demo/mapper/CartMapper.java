package com.project.demo.mapper;

import java.math.BigDecimal;
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
import com.project.demo.mapper.util.PriceCalculationUtils;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { ProductMapper.class,
        PriceCalculationUtils.class })
public interface CartMapper {

    // --------- Cart ---------
    // 1-1.欄位對應名稱不同
    @Mapping(target = "cartUuid", source = "uuid")
    // 1-2.巢狀物件的欄位，只需要取得物件指定欄位
    @Mapping(target = "userUuid", source = "user.uuid")
    // 2.複雜的物件集合轉換
    @Mapping(target = "items", source = "items", qualifiedByName = "toCartItemDTOs")
    // 3.動態計算的欄位
    @Mapping(target = "subtotal", source = "items", qualifiedByName = "calculateSubtotal")
    @Mapping(target = "shipping", source = "items", qualifiedByName = "calculateShipping")
    @Mapping(target = "total", source = "items", qualifiedByName = "calculateTotal")
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
        String imageUrl = product.getImageUrl() == null ? "" : product.getImageUrl();
        BigDecimal discountPercentage = product.getDiscountPercentage() == null ? BigDecimal.ZERO
                : product.getDiscountPercentage();

        return new CartItemDTO(
                product.getName(),
                product.getPrice(),
                discountPercentage,
                discountPrice,
                imageUrl,
                cartItem.getQuantity(),
                product.getUuid());
    }
}
