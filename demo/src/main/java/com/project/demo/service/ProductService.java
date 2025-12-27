package com.project.demo.service;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

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

		List<ProductResponseDTO> productDTO = productMapper.toResponseDTOs(productPage.getContent());

		return new PaginatedResponse<>(
				productDTO,
				productPage.getNumber(),
				productPage.getSize(),
				productPage.getTotalElements(),
				productPage.getTotalPages());
	}

	// Get product by uuid
	public ProductResponseDTO getProductByUuid(UUID productUuid) {
		Product product = productRepository.findByUuid(productUuid)
				.orElseThrow(() -> new EntityNotFoundException("Product not found with uuid: " + productUuid));

		return productMapper.toProductResponseDTO(product);
	}

	// Record sale
	@Transactional
	public Product recordSale(UUID productUuid, int quantity) {
		Product product = checkAndGetProduct(productUuid, quantity);

		int newStock = product.getStock() - quantity;
		product.setStock(newStock);

		if (newStock == 0) {
			product.setAvailabilityStatus(AvailabilityStatus.OUT_OF_STOCK);
		}

		product.setTotalSold(product.getTotalSold() + quantity);
		product.setUpdatedAt(LocalDateTime.now());

		return productRepository.save(product);
	}

	// Check and get product
	@Transactional(readOnly = true)
	public Product checkAndGetProduct(UUID productUuid, int requestedQuantity) {
		Product product = productRepository.findByUuid(productUuid)
				.orElseThrow(() -> new EntityNotFoundException("Product not found with uuid: " + productUuid));

		if (product.getStock() < requestedQuantity) {
			throw new InsufficientStockException(product.getName(), product.getStock());
		}

		return product;
	}

	// Release stock
	@Transactional
	public void releaseStockForOrder(Order order) {
		List<Product> productsToUpdate = order.getItems().stream().map(item -> {
			Product product = item.getProduct();
			int quantity = item.getQuantity();

			int newStock = product.getStock() + quantity;
			product.setStock(newStock);

			if (newStock > 0 && product.getAvailabilityStatus() == AvailabilityStatus.OUT_OF_STOCK) {
				product.setAvailabilityStatus(AvailabilityStatus.IN_STOCK);
			}

			product.setTotalSold(product.getTotalSold() - quantity);
			product.setUpdatedAt(LocalDateTime.now());
			
			return product;
		}).collect(Collectors.toList());
		
		productRepository.saveAll(productsToUpdate);
	}
}
