package com.project.demo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.project.demo.enumeration.AvailabilityStatus;
import com.project.demo.enumeration.Category;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Index;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "products", indexes = {
		@Index(name = "idx_product_uuid", columnList = "uuid")
})
@Getter
@Setter
@ToString
public class Product {

	// ===== Primary Key =====
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id")
	private Long id;

	// ===== Basic Info =====
	@Column(name = "uuid", nullable = false, unique = true, updatable = false)
	private UUID uuid;

	@Column(name = "name", nullable = false)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name = "availability_status", nullable = false)
	private AvailabilityStatus availabilityStatus;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(name = "sku", nullable = false, unique = true, updatable = false)
	private String sku;

	@Column(name = "price", nullable = false, precision = 10, scale = 2)
	private BigDecimal price;

	@Column(name = "discount_percentage", precision = 5, scale = 2)
	private BigDecimal discountPercentage;

	@Column(name = "description", nullable = false)
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(name = "category", nullable = false)
	private Category category;

	@Column(name = "stock", nullable = false)
	private Integer stock;

	@Column(name = "total_sold", nullable = false)
	private Integer totalSold = 0;

	@Column(name = "image_url")
	private String imageUrl;
}
