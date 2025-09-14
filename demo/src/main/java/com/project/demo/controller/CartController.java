package com.project.demo.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

import java.util.Map;
import java.util.UUID;

import com.project.demo.service.CartService;
import com.project.demo.dto.cart.CartAddItemRequestDTO;
import com.project.demo.dto.cart.CartUpdateItemRequestDTO;
import com.project.demo.dto.cart.CartResponseDTO;
import com.project.demo.model.User;
import com.project.demo.exception.InsufficientStockException;

import lombok.RequiredArgsConstructor;

import static com.project.demo.data.PathConstantData.API_CURRENT_USER_CART;
import static com.project.demo.data.PathConstantData.API_CURRENT_USER_CART_ITEMS;
import static com.project.demo.data.PathConstantData.API_CURRENT_USER_CART_ITEM_BY_UUID;

@RestController
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // --------- Cart ---------

    /*
     * POST method
     */
    @PostMapping(API_CURRENT_USER_CART_ITEMS)
    public ResponseEntity<?> addItemToCart(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CartAddItemRequestDTO dto) {
        try {
            CartResponseDTO response = cartService.addItemToCart(user.getUuid(), dto);

            return ResponseEntity.ok(response);
        } catch (InsufficientStockException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound()
                    .build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An unexpected error occurred. Please try again later!"));
        }
    }

    /*
     * GET method
     */
    @GetMapping(API_CURRENT_USER_CART)
    public ResponseEntity<?> getCartByUserUuid(@AuthenticationPrincipal User user) {
        try {
            CartResponseDTO response = cartService.getCartByUserUuid(user.getUuid());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound()
                    .build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An unexpected error occurred. Please try again later!"));
        }
    }

    // --------- CartItem ---------

    /*
     * PUT
     */
    @PutMapping(API_CURRENT_USER_CART_ITEM_BY_UUID)
    public ResponseEntity<?> updateCartItemQuantity(
            @AuthenticationPrincipal User user,
            @PathVariable UUID productUuid,
            @Valid @RequestBody CartUpdateItemRequestDTO dto) {
        try {
            CartResponseDTO response = cartService.updateCartItemQuantity(user.getUuid(), productUuid, dto.quantity());

            return ResponseEntity.ok(response);
        } catch (InsufficientStockException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound()
                    .build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An unexpected error occurred. Please try again later!"));
        }
    }

    /*
     * DELETE
     */
    @DeleteMapping(API_CURRENT_USER_CART_ITEM_BY_UUID)
    public ResponseEntity<?> removeItemFromCart(
            @AuthenticationPrincipal User user,
            @PathVariable UUID productUuid) {
        try {
            CartResponseDTO response = cartService.removeItemFromCart(user.getUuid(), productUuid);

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
