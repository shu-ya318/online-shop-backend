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

    @Mapping(target = "discountPrice", source = "product", qualifiedByName = "calculateDiscountPrice")
    ProductResponseDTO toProductResponseDTO(Product product);

    // Utils
    @Named("calculateDiscountPrice")
    default BigDecimal calculateDiscountPrice(Product product) {
        if (product == null || product.getPrice() == null) {
            return null;
        }

        BigDecimal price = product.getPrice();
        BigDecimal discountPercentage = product.getDiscountPercentage();

        if (discountPercentage == null) {
            return price;
        }

        BigDecimal discountMultiplier = discountPercentage.divide(new BigDecimal("100"));
        BigDecimal priceMultiplier = BigDecimal.ONE.subtract(discountMultiplier);
        BigDecimal discountedPrice = price.multiply(priceMultiplier);

        return discountedPrice.setScale(2, RoundingMode.HALF_UP);
    }
}
