package com.project.demo.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.project.demo.dto.project.ProductResponseDTO;
import com.project.demo.mapper.ProductMapper;
import com.project.demo.model.Product;
import com.project.demo.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    // Get product by uuid
    public ProductResponseDTO getProductByUuid(UUID uuid) {
        Product product = productRepository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("Product not found with uuid: " + uuid));

        return productMapper.toProductResponseDTO(product);
    }
}
