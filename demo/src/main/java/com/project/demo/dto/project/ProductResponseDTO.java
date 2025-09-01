package com.project.demo.dto.project;

import java.math.BigDecimal;
//import java.time.LocalDateTime;
import java.util.UUID;

import com.project.demo.enumeration.AvailabilityStatus;
import com.project.demo.enumeration.Category;

public record ProductResponseDTO(
	UUID uuid, 
	String name, 
	AvailabilityStatus availabilityStatus, 
//	LocalDateTime createdAt, 
//	LocalDateTime updatedAt, 
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
