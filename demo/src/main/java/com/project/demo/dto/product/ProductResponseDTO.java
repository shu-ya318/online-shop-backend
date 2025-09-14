package com.project.demo.dto.product;

import java.math.BigDecimal;
import java.util.UUID;

import com.project.demo.enumeration.AvailabilityStatus;
import com.project.demo.enumeration.Category;

public record ProductResponseDTO(
	UUID uuid, 
	String name, 
	AvailabilityStatus availabilityStatus, 
	String sku,
	BigDecimal price,
	BigDecimal discountPercentage,
	BigDecimal discountPrice,
	String description, 
	Category category, 
	Integer stock, 
	Integer totalSold,
	String imageUrl) {
}
