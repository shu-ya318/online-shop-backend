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
import com.project.demo.enumeration.SortBy;
import com.project.demo.dto.product.ProductResponseDTO;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.UUID;

import static com.project.demo.data.PathConstantData.API_PUBLIC;
import static com.project.demo.data.PathConstantData.API_PRODUCTS;
import static com.project.demo.data.PathConstantData.API_PRODUCT_BY_UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_PUBLIC)
@Tag(name = "Product")
public class ProductController {

    private final ProductService productService;

    /*
     * GET method
     */

    @Operation(summary = "Get products", description = "Retrieves a paginated list of products with optional filtering and sorting.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = PaginatedResponse.class),
                    examples = @ExampleObject(value = "{\"content\":[{\"uuid\":\"string\",\"name\":\"string\",\"availabilityStatus\":\"IN_STOCK\",\"sku\":\"string\",\"price\":0.00,\"discountPercentage\":0.00,\"discountPrice\":0.00,\"description\":\"string\",\"category\":\"GRAINS\",\"stock\":0,\"totalSold\":0,\"imageUrl\":\"string\"}],\"currentPage\":0,\"size\":16,\"totalElements\":1,\"totalPages\":1}"))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":400,\"error\":\"Bad Request\",\"message\":\"Invalid page number\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"An unexpected error occurred!\"}")))
    })
    @GetMapping(API_PRODUCTS)
    public ResponseEntity<PaginatedResponse<ProductResponseDTO>> getProducts(
            @Parameter(description = "Search keyword") @RequestParam(name = "keyword", required = false) String keyword,
            @Parameter(description = "Product category") @RequestParam(name = "category", required = false) Category category,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "16") int size,
            @Parameter(description = "Sort field") @RequestParam(name = "sortBy", defaultValue = "updatedAt") SortBy sortBy,
            @Parameter(description = "Sort direction") @RequestParam(name = "sortDirection", defaultValue = "DESC") Sort.Direction sortDirection) {
        ProductFilterRequestDTO filter = new ProductFilterRequestDTO(keyword, category);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy.getField()));

        PaginatedResponse<ProductResponseDTO> responseDTO = productService.getProducts(filter, pageable);

        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Get product by UUID", description = "Retrieves details of a specific product by its UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product retrieved successfully", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = ProductResponseDTO.class),
                    examples = @ExampleObject(value = "{\"uuid\":\"string\",\"name\":\"string\",\"availabilityStatus\":\"IN_STOCK\",\"sku\":\"string\",\"price\":0.00,\"discountPercentage\":0.00,\"discountPrice\":0.00,\"description\":\"string\",\"category\":\"GRAINS\",\"stock\":0,\"totalSold\":0,\"imageUrl\":\"string\"}"))),
            @ApiResponse(responseCode = "404", description = "Product not found", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":404,\"error\":\"Not Found\",\"message\":\"Product not found with the given UUID\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"An unexpected error occurred!\"}")))
    })
    @GetMapping(API_PRODUCT_BY_UUID)
    public ResponseEntity<ProductResponseDTO> getProductByUuid(
            @Parameter(description = "UUID of the product") @PathVariable("productUuid") UUID uuid) {
        ProductResponseDTO responseDTO = productService.getProductByUuid(uuid);

        return ResponseEntity.ok(responseDTO);
    }
}
