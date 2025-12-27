package com.project.demo.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.demo.dto.common.PaginatedResponse;
import com.project.demo.dto.product.ProductFilterRequestDTO;
import com.project.demo.service.ProductService;
import com.project.demo.enumeration.Category;
import com.project.demo.dto.product.ProductResponseDTO;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

import static com.project.demo.data.PathConstantData.API_PUBLIC;
import static com.project.demo.data.PathConstantData.API_PRODUCTS;
import static com.project.demo.data.PathConstantData.API_PRODUCT_BY_UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_PUBLIC)
public class ProductController {

    private final ProductService productService;

    /*
     * GET method
     */

    @GetMapping(API_PRODUCTS)
    public ResponseEntity<PaginatedResponse<ProductResponseDTO>> getProducts(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "category", required = false) Category category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "16") int size,
            @RequestParam(name = "sortBy", defaultValue = "updatedAt") String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "DESC") Sort.Direction sortDirection) {
        ProductFilterRequestDTO filter = new ProductFilterRequestDTO(keyword, category);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        PaginatedResponse<ProductResponseDTO> responseDTO = productService.getProducts(filter, pageable);

        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping(API_PRODUCT_BY_UUID)
    public ResponseEntity<ProductResponseDTO> getProductByUuid(@PathVariable("productUuid") UUID uuid) {
        ProductResponseDTO responseDTO = productService.getProductByUuid(uuid);

        return ResponseEntity.ok(responseDTO);
    }
}
