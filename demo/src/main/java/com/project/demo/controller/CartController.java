package com.project.demo.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

import java.util.UUID;

import com.project.demo.service.CartService;
import com.project.demo.dto.cart.CartItemAddRequestDTO;
import com.project.demo.dto.cart.CartItemQtyUpdateRequestDTO;
import com.project.demo.dto.cart.CartResponseDTO;
import com.project.demo.model.User;

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
    public ResponseEntity<CartResponseDTO> addItemToCart(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CartItemAddRequestDTO dto) {
        CartResponseDTO responseDTO = cartService.addItemToCart(user.getUuid(), dto);
        
        return ResponseEntity.ok(responseDTO);
    }

    /*
     * GET method
     */

    @GetMapping(API_CURRENT_USER_CART)
    public ResponseEntity<CartResponseDTO> getCartByUserUuid(@AuthenticationPrincipal User user) {
        CartResponseDTO responseDTO = cartService.getCartByUserUuid(user.getUuid());
        
        return ResponseEntity.ok(responseDTO);
    }

    // --------- CartItem ---------

    /*
     * PUT method
     */

    @PutMapping(API_CURRENT_USER_CART_ITEM_BY_UUID)
    public ResponseEntity<CartResponseDTO> updateCartItemQty(
            @AuthenticationPrincipal User user,
            @PathVariable UUID cartItemUuid,
            @Valid @RequestBody CartItemQtyUpdateRequestDTO dto) {
        CartResponseDTO responseDTO = cartService.updateCartItemQty(user.getUuid(), cartItemUuid, dto.quantity());
        
        return ResponseEntity.ok(responseDTO);
    }

    /*
     * DELETE
     */

    @DeleteMapping(API_CURRENT_USER_CART_ITEM_BY_UUID)
    public ResponseEntity<CartResponseDTO> removeItemFromCart(
            @AuthenticationPrincipal User user,
            @PathVariable UUID cartItemUuid) {
        CartResponseDTO responseDTO = cartService.removeItemFromCart(user.getUuid(), cartItemUuid);

        return ResponseEntity.ok(responseDTO);
    }
}
