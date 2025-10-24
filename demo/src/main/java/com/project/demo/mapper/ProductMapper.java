package com.project.demo.mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.project.demo.dto.product.ProductResponseDTO;
import com.project.demo.model.Product;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {
    List<ProductResponseDTO> toResponseDTOs(List<Product> products);

    @Mapping(target = "discountPrice", expression = "java(calculateDiscountPrice(product))")
    @Mapping(target = "imageUrl", expression = "java(product.getImageUrl() == null ? \"\" : product.getImageUrl())")
    @Mapping(target = "discountPercentage", expression = "java(product.getDiscountPercentage() == null ? java.math.BigDecimal.ZERO : product.getDiscountPercentage())")
    ProductResponseDTO toProductResponseDTO(Product product);

    // ----- Utils -----
    @Named("calculateDiscountPrice")
    default BigDecimal calculateDiscountPrice(Product product) {
        if (product == null || product.getPrice() == null) {
            return null;
        }

        BigDecimal price = product.getPrice();
        BigDecimal discountPercentage = product.getDiscountPercentage();
        BigDecimal finalPrice;

        if (discountPercentage == null) {
            finalPrice = price;
        } else {
            BigDecimal discountMultiplier = discountPercentage.divide(new BigDecimal("100"));
            BigDecimal priceMultiplier = BigDecimal.ONE.subtract(discountMultiplier);
            finalPrice = price.multiply(priceMultiplier);
        }

        BigDecimal result = finalPrice.setScale(0, RoundingMode.HALF_UP);

        return result;
    }
}
