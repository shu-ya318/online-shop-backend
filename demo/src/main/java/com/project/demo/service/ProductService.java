package com.project.demo.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
//import org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;

import com.project.demo.dto.common.PaginatedResponse;
import com.project.demo.dto.project.ProductFilterRequestDTO;
import com.project.demo.dto.project.ProductResponseDTO;
//import com.project.demo.enumeration.Category;
import com.project.demo.mapper.ProductMapper;
import com.project.demo.model.Product;
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
		// Sort sortOrder = parseSort(sort);
		// Pageable pageable = PageRequest.of(page, size, sortOrder);

		Specification<Product> specification = ProductSpecifications.doFilter(filter);

		Page<Product> productPage = productRepository.findAll(specification, pageable);

		List<ProductResponseDTO> productDTOs = productMapper.toResponseDTO(productPage.getContent());

		return new PaginatedResponse<>(
				productDTOs,
				productPage.getNumber(),
				productPage.getSize(),
				productPage.getTotalElements(),
				productPage.getTotalPages());
	}

	// Get product by uuid
	public ProductResponseDTO getProductByUuid(UUID uuid) {
		Product product = productRepository.findByUuid(uuid)
				.orElseThrow(() -> new RuntimeException("Product not found with uuid: " + uuid));

		return productMapper.toProductResponseDTO(product);
	}
}
