package com.project.demo.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.project.demo.service.ProductService;

import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.UUID;

import static com.project.demo.data.PathConstantData.API_PUBLIC;
import static com.project.demo.data.PathConstantData.API_PRODUCT_BY_UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_PUBLIC)
public class ProductController {

    private final ProductService productService;

    /*
     * GET method
     */
    @GetMapping(API_PRODUCT_BY_UUID)
    public ResponseEntity<?> getProductByUuid(@PathVariable UUID uuid) {
        try {
            return ResponseEntity.ok(productService.getProductByUuid(uuid));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An unexpected error occurred. Please try again later!"));
        }
    }
}
