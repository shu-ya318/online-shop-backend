package com.project.demo.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.project.demo.dto.order.OrderCreateRequestDTO;
import com.project.demo.dto.order.OrderResponseDTO;
import com.project.demo.model.User;
import com.project.demo.service.OrderService;
import com.project.demo.dto.common.PaginatedResponse;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.Parameter;

import static com.project.demo.data.PathConstantData.API_CURRENT_USER_ORDERS;
import static com.project.demo.data.PathConstantData.API_CURRENT_USER_ORDER_BY_UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Order")
public class OrderController {

    private final OrderService orderService;

    // --------- Order ---------

    /*
     * POST method
     */

    @Operation(summary = "Create order", description = "Creates a new order for the current user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order created successfully", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = OrderResponseDTO.class),
                    examples = @ExampleObject(value = "{\"orderUuid\":\"string\",\"status\":\"PENDING\",\"items\":[{\"productName\":\"string\",\"unitPrice\":0.00,\"discountPercentage\":0.00,\"discountPrice\":0.00,\"imageUrl\":\"string\",\"quantity\":1,\"productUuid\":\"string\"}],\"subtotal\":0.00,\"shipping\":0.00,\"total\":0.00,\"totalQuantity\":1,\"userUuid\":\"string\",\"payment\":null}"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":400,\"error\":\"Bad Request\",\"message\":\"Invalid address format\"}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Full authentication is required to access this resource\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"An unexpected error occurred!\"}")))
    })
    @PostMapping(API_CURRENT_USER_ORDERS)
    public ResponseEntity<OrderResponseDTO> createOrder(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody OrderCreateRequestDTO dto) {
        OrderResponseDTO responseDTO = orderService.createOrder(user.getUuid(), dto);

        return ResponseEntity.ok(responseDTO);
    }

    /*
     * GET method
     */

    @Operation(summary = "Get user orders", description = "Retrieves a paginated list of orders for the current user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = PaginatedResponse.class),
                    examples = @ExampleObject(value = "{\"content\":[{\"orderUuid\":\"string\",\"status\":\"COMPLETED\",\"items\":[{\"productName\":\"string\",\"unitPrice\":0.00,\"discountPercentage\":0.00,\"discountPrice\":0.00,\"imageUrl\":\"string\",\"quantity\":1,\"productUuid\":\"string\"}],\"subtotal\":0.00,\"shipping\":0.00,\"total\":0.00,\"totalQuantity\":1,\"userUuid\":\"string\",\"payment\":{\"uuid\":\"string\",\"status\":\"SUCCESS\",\"method\":\"PAYPAL\",\"amount\":0.00,\"currency\":\"TWD\"}}],\"currentPage\":0,\"size\":8,\"totalElements\":1,\"totalPages\":1}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Full authentication is required to access this resource\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"An unexpected error occurred!\"}")))
    })
    @GetMapping(API_CURRENT_USER_ORDERS)
    public ResponseEntity<PaginatedResponse<OrderResponseDTO>> getOrders(
            @AuthenticationPrincipal User user,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "8") int size) {
        Pageable pageable = PageRequest.of(page, size);

        PaginatedResponse<OrderResponseDTO> responseDTO = orderService.getOrders(user.getUuid(), pageable);

        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Get order by UUID", description = "Retrieves details of a specific order by its UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order retrieved successfully", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = OrderResponseDTO.class),
                    examples = @ExampleObject(value = "{\"orderUuid\":\"string\",\"status\":\"COMPLETED\",\"items\":[{\"productName\":\"string\",\"unitPrice\":0.00,\"discountPercentage\":0.00,\"discountPrice\":0.00,\"imageUrl\":\"string\",\"quantity\":1,\"productUuid\":\"string\"}],\"subtotal\":0.00,\"shipping\":0.00,\"total\":0.00,\"totalQuantity\":1,\"userUuid\":\"string\",\"payment\":{\"uuid\":\"string\",\"status\":\"SUCCESS\",\"method\":\"PAYPAL\",\"amount\":0.00,\"currency\":\"TWD\"}}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Full authentication is required to access this resource\"}"))),
            @ApiResponse(responseCode = "404", description = "Order not found", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":404,\"error\":\"Not Found\",\"message\":\"Order not found\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"An unexpected error occurred!\"}")))
    })
    @GetMapping(API_CURRENT_USER_ORDER_BY_UUID)
    public ResponseEntity<OrderResponseDTO> getOrderByUuid(
            @AuthenticationPrincipal User user,
            @Parameter(description = "UUID of the order") @PathVariable UUID orderUuid) {
        OrderResponseDTO responseDTO = orderService.getOrderByUuid(orderUuid);

        return ResponseEntity.ok(responseDTO);
    }

    /*
     * PUT method
     */

    @Operation(summary = "Cancel order", description = "Cancels an existing order by its UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order canceled successfully", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = OrderResponseDTO.class),
                    examples = @ExampleObject(value = "{\"orderUuid\":\"string\",\"status\":\"CANCELLED\",\"items\":[{\"productName\":\"string\",\"unitPrice\":0.00,\"discountPercentage\":0.00,\"discountPrice\":0.00,\"imageUrl\":\"string\",\"quantity\":1,\"productUuid\":\"string\"}],\"subtotal\":0.00,\"shipping\":0.00,\"total\":0.00,\"totalQuantity\":1,\"userUuid\":\"string\",\"payment\":null}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Full authentication is required to access this resource\"}"))),
            @ApiResponse(responseCode = "404", description = "Order not found", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":404,\"error\":\"Not Found\",\"message\":\"Order not found\"}"))),
            @ApiResponse(responseCode = "409", description = "Order cannot be canceled", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":409,\"error\":\"Conflict\",\"message\":\"Order cannot be canceled as it is already shipped\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"An unexpected error occurred!\"}")))
    })
    @PutMapping(API_CURRENT_USER_ORDER_BY_UUID)
    public ResponseEntity<OrderResponseDTO> cancelOrderByUuid(
            @AuthenticationPrincipal User user,
            @Parameter(description = "UUID of the order") @PathVariable UUID orderUuid) {
        OrderResponseDTO responseDTO = orderService.cancelOrderByUuid(orderUuid);

        return ResponseEntity.ok(responseDTO);
    }
}
