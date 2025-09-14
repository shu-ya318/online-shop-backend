package com.project.demo.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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

import java.util.Map;
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
    public ResponseEntity<?> getProducts(
            @RequestParam(name = "filter[keyword]", required = false) String keyword,
            @RequestParam(name = "filter[category]", required = false) Category category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "16") int size,
            @RequestParam(name = "sortBy", defaultValue = "updatedAt") String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "DESC") Sort.Direction sortDirection) {
        try {
            ProductFilterRequestDTO filter = new ProductFilterRequestDTO(keyword, category);
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

            PaginatedResponse<ProductResponseDTO> response = productService.getProducts(filter, pageable);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An unexpected error occurred. Please try again later!"));
        }
    }

    @GetMapping(API_PRODUCT_BY_UUID)
    public ResponseEntity<?> getProductByUuid(@PathVariable UUID uuid) {
        try {
            ProductResponseDTO response = productService.getProductByUuid(uuid);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound()
                    .build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An unexpected error occurred. Please try again later!"));
        }
    }
}
