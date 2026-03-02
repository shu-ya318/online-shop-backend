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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.Parameter;

import static com.project.demo.data.PathConstantData.API_CURRENT_USER_CART;
import static com.project.demo.data.PathConstantData.API_CURRENT_USER_CART_ITEMS;
import static com.project.demo.data.PathConstantData.API_CURRENT_USER_CART_ITEM_BY_UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Cart")
public class CartController {

    private final CartService cartService;

    // --------- Cart ---------

    /*
     * POST method
     */

    @Operation(summary = "Add item to cart", description = "Adds a new item to the user's shopping cart.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item added successfully", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = CartResponseDTO.class),
                    examples = @ExampleObject(value = "{\"cartUuid\":\"string\",\"items\":[{\"cartItemUuid\":\"string\",\"productName\":\"string\",\"price\":0.00,\"discountPercentage\":0.00,\"discountPrice\":0.00,\"imageUrl\":\"string\",\"quantity\":1,\"productUuid\":\"string\"}],\"subtotal\":0.00,\"shipping\":0.00,\"total\":0.00,\"totalQuantity\":1,\"userUuid\":\"string\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":400,\"error\":\"Bad Request\",\"message\":\"Invalid quantity: must be greater than 0\"}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Full authentication is required to access this resource\"}"))),
            @ApiResponse(responseCode = "404", description = "Product not found", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":404,\"error\":\"Not Found\",\"message\":\"Product not found\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"An unexpected error occurred!\"}")))
    })
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

    @Operation(summary = "Get cart", description = "Retrieves the current user's shopping cart.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart retrieved successfully", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = CartResponseDTO.class),
                    examples = @ExampleObject(value = "{\"cartUuid\":\"string\",\"items\":[{\"cartItemUuid\":\"string\",\"productName\":\"string\",\"price\":0.00,\"discountPercentage\":0.00,\"discountPrice\":0.00,\"imageUrl\":\"string\",\"quantity\":1,\"productUuid\":\"string\"}],\"subtotal\":0.00,\"shipping\":0.00,\"total\":0.00,\"totalQuantity\":1,\"userUuid\":\"string\"}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Full authentication is required to access this resource\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"An unexpected error occurred!\"}")))
    })
    @GetMapping(API_CURRENT_USER_CART)
    public ResponseEntity<CartResponseDTO> getCartByUserUuid(@AuthenticationPrincipal User user) {
        CartResponseDTO responseDTO = cartService.getCartByUserUuid(user.getUuid());
        
        return ResponseEntity.ok(responseDTO);
    }

    // --------- CartItem ---------

    /*
     * PUT method
     */

    @Operation(summary = "Update cart item quantity", description = "Updates the quantity of a specific cart item.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart item updated successfully", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = CartResponseDTO.class),
                    examples = @ExampleObject(value = "{\"cartUuid\":\"string\",\"items\":[{\"cartItemUuid\":\"string\",\"productName\":\"string\",\"price\":0.00,\"discountPercentage\":0.00,\"discountPrice\":0.00,\"imageUrl\":\"string\",\"quantity\":2,\"productUuid\":\"string\"}],\"subtotal\":0.00,\"shipping\":0.00,\"total\":0.00,\"totalQuantity\":2,\"userUuid\":\"string\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid quantity or input", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":400,\"error\":\"Bad Request\",\"message\":\"Invalid quantity\"}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Full authentication is required to access this resource\"}"))),
            @ApiResponse(responseCode = "404", description = "Cart item not found", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":404,\"error\":\"Not Found\",\"message\":\"Cart item not found\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"An unexpected error occurred!\"}")))
    })
    @PutMapping(API_CURRENT_USER_CART_ITEM_BY_UUID)
    public ResponseEntity<CartResponseDTO> updateCartItemQty(
            @AuthenticationPrincipal User user,
            @Parameter(description = "UUID of the cart item") @PathVariable UUID cartItemUuid,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "New quantity for the item", required = true,
                    content = @Content(schema = @Schema(implementation = CartItemQtyUpdateRequestDTO.class)))
            @Valid @RequestBody CartItemQtyUpdateRequestDTO dto) {
        CartResponseDTO responseDTO = cartService.updateCartItemQty(user.getUuid(), cartItemUuid, dto.quantity());
        
        return ResponseEntity.ok(responseDTO);
    }

    /*
     * DELETE
     */

    @Operation(summary = "Remove item from cart", description = "Removes a specific item from the shopping cart.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item removed successfully", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = CartResponseDTO.class),
                    examples = @ExampleObject(value = "{\"cartUuid\":\"string\",\"items\":[],\"subtotal\":0.00,\"shipping\":0.00,\"total\":0.00,\"totalQuantity\":0,\"userUuid\":\"string\"}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Full authentication is required to access this resource\"}"))),
            @ApiResponse(responseCode = "404", description = "Cart item not found", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":404,\"error\":\"Not Found\",\"message\":\"Cart item not found\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"An unexpected error occurred!\"}")))
    })
    @DeleteMapping(API_CURRENT_USER_CART_ITEM_BY_UUID)
    public ResponseEntity<CartResponseDTO> removeItemFromCart(
            @AuthenticationPrincipal User user,
            @Parameter(description = "UUID of the cart item") @PathVariable UUID cartItemUuid) {
        CartResponseDTO responseDTO = cartService.removeItemFromCart(user.getUuid(), cartItemUuid);

        return ResponseEntity.ok(responseDTO);
    }
}
