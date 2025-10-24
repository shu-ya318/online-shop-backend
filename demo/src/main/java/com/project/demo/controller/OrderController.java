package com.project.demo.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

import static com.project.demo.data.PathConstantData.API_CURRENT_USER_ORDERS;
import static com.project.demo.data.PathConstantData.API_CURRENT_USER_ORDER_BY_UUID;
import static com.project.demo.data.PathConstantData.API_CURRENT_USER_ORDER_CANCEL_BY_UUID;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // --------- Order ---------

    /*
     * POST method
     */

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

    @GetMapping(API_CURRENT_USER_ORDERS)
    public ResponseEntity<PaginatedResponse<OrderResponseDTO>> getOrders(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {
        Pageable pageable = PageRequest.of(page, size);

        PaginatedResponse<OrderResponseDTO> responseDTO = orderService.getOrders(user.getUuid(), pageable);

        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping(API_CURRENT_USER_ORDER_BY_UUID)
    public ResponseEntity<OrderResponseDTO> getOrderByUuid(
            @AuthenticationPrincipal User user,
            @PathVariable UUID uuid) {
        OrderResponseDTO responseDTO = orderService.getOrderByUuid(uuid);

        return ResponseEntity.ok(responseDTO);
    }

    /*
     * PATCH method
     */

    @PatchMapping(API_CURRENT_USER_ORDER_CANCEL_BY_UUID)
    public ResponseEntity<OrderResponseDTO> cancelOrderByUuid(
            @AuthenticationPrincipal User user,
            @PathVariable UUID uuid) {
        OrderResponseDTO responseDTO = orderService.cancelOrderByUuid(uuid);

        return ResponseEntity.ok(responseDTO);
    }
}
