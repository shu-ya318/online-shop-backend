package com.project.demo.service;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.demo.dto.common.PaginatedResponse;
import com.project.demo.dto.product.ProductFilterRequestDTO;
import com.project.demo.dto.product.ProductResponseDTO;
import com.project.demo.enumeration.AvailabilityStatus;
import com.project.demo.exception.EntityNotFoundException;
import com.project.demo.exception.InsufficientStockException;
import com.project.demo.mapper.ProductMapper;
import com.project.demo.model.Product;
import com.project.demo.model.Order;
import com.project.demo.model.OrderItem;
import com.project.demo.repository.ProductRepository;
import com.project.demo.specification.ProductSpecifications;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final ProductMapper productMapper;

	// Get products
	public PaginatedResponse<ProductResponseDTO> getProducts(ProductFilterRequestDTO filter, Pageable pageable) {
		Specification<Product> specification = ProductSpecifications.doFilter(filter);

		Page<Product> productPage = productRepository.findAll(specification, pageable);

		List<ProductResponseDTO> productDTO = productMapper.toResponseDTOs(productPage.getContent()).stream()
				.map(this::updateAvailabilityByStock)
				.toList();

		PaginatedResponse<ProductResponseDTO> responseDTO = new PaginatedResponse<>(
				productDTO,
				productPage.getNumber(),
				productPage.getSize(),
				productPage.getTotalElements(),
				productPage.getTotalPages());

		return responseDTO;
	}

	// Get product by uuid
	public ProductResponseDTO getProductByUuid(UUID uuid) {
		Product product = productRepository.findByUuid(uuid)
				.orElseThrow(() -> new EntityNotFoundException("Product not found with uuid: " + uuid));

		ProductResponseDTO productDTO = productMapper.toProductResponseDTO(product);

		ProductResponseDTO responseDTO = updateAvailabilityByStock(productDTO);

		return responseDTO;
	}

	// Record Sale
	@Transactional
	public Product recordSale(UUID productUuid, int quantity) {
		Product product = productRepository.findByUuid(productUuid)
				.orElseThrow(() -> new EntityNotFoundException("Product not found with uuid: " + productUuid));

		if (product.getStock() < quantity) {
			throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
		}

		int newStock = product.getStock() - quantity;
		product.setStock(newStock);

		if (newStock == 0) {
			product.setAvailabilityStatus(AvailabilityStatus.OUT_OF_STOCK);
		}

		product.setTotalSold(product.getTotalSold() + quantity);
		product.setUpdatedAt(LocalDateTime.now());

		return product;
	}

	// Release Stock
	@Transactional
	public void releaseStockForOrder(Order order) {
		for (OrderItem item : order.getItems()) {
			Product product = item.getProduct();
			int quantity = item.getQuantity();

			int newStock = product.getStock() + quantity;
			product.setStock(newStock);

			if (newStock > 0 && product.getAvailabilityStatus() == AvailabilityStatus.OUT_OF_STOCK) {
				product.setAvailabilityStatus(AvailabilityStatus.IN_STOCK);
			}

			product.setTotalSold(product.getTotalSold() - quantity);
			product.setUpdatedAt(LocalDateTime.now());
			
			productRepository.save(product);
		}
	}

	// ----- Private Helper Method -----

	// Update availability by stock
	private ProductResponseDTO updateAvailabilityByStock(ProductResponseDTO dto) {
		boolean shouldUpdateToOutOfStock = dto.stock() != null
				&& dto.stock() <= 0
				&& dto.availabilityStatus() != AvailabilityStatus.OUT_OF_STOCK;
		
		if (shouldUpdateToOutOfStock) {
			ProductResponseDTO responseDTO = new ProductResponseDTO(
					dto.uuid(),
					dto.name(),
					AvailabilityStatus.OUT_OF_STOCK,
					dto.sku(),
					dto.price(),
					dto.discountPercentage(),
					dto.discountPrice(),
					dto.description(),
					dto.category(),
					dto.stock(),
					dto.totalSold(),
					dto.imageUrl());

			return responseDTO;
		}

		return dto;
	}
}
