package com.project.demo.dto.product;

import java.math.BigDecimal;
import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;

import com.project.demo.enumeration.AvailabilityStatus;
import com.project.demo.enumeration.Category;

public record ProductResponseDTO(
	@Schema(description = "Product unique identifier", example = "a1b2c3d4-e5f6-7a8b-9c0d-e1f2a3b4c5d6")
	UUID uuid, 
	@Schema(description = "Product name", example = "Professional Camera Lens")
	String name, 
	@Schema(description = "Availability of the product")
	AvailabilityStatus availabilityStatus, 
	@Schema(description = "Stock Keeping Unit", example = "SKU-12345")
	String sku,
	@Schema(description = "Unit price", example = "999.00")
	BigDecimal price,
	@Schema(description = "Discount percentage", example = "10.00")
	BigDecimal discountPercentage,
	@Schema(description = "Final price after discount", example = "899.10")
	BigDecimal discountPrice,
	@Schema(description = "Product long description", example = "A high-quality 50mm f/1.8 lens for portrait photography.")
	String description, 
	@Schema(description = "Product category")
	Category category, 
	@Schema(description = "Number of items in stock", example = "50")
	Integer stock, 
	@Schema(description = "Total number of items sold", example = "120")
	Integer totalSold,
	@Schema(description = "URL of the product image", example = "https://example.com/images/lens.jpg")
	String imageUrl) {
}
