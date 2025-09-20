package com.project.demo.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

import com.project.demo.dto.common.PaginatedResponse;
import com.project.demo.model.Order;
import com.project.demo.model.User;
import com.project.demo.model.OrderItem;
import com.project.demo.model.Product;
import com.project.demo.dto.order.OrderResponseDTO;
import com.project.demo.enumeration.OrderStatus;
import com.project.demo.exception.EntityNotFoundException;
import com.project.demo.dto.order.OrderCreateRequestDTO;
import com.project.demo.repository.OrderRepository;
import com.project.demo.repository.UserRepository;
import com.project.demo.mapper.OrderMapper;

import org.springframework.data.domain.Page;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartService cartService;
    private final ProductService productService;
    private final OrderMapper orderMapper;

    // --------- Order ---------

    // Create order
    @Transactional
    public OrderResponseDTO createOrder(UUID userUuid, OrderCreateRequestDTO dto) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User not found with uuid: " + userUuid));

        Order order = new Order();

        order.setUser(user);
        order.setUuid(UUID.randomUUID());
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setRecipientName(dto.recipientName());
        order.setRecipientPhoneNumber(dto.recipientPhoneNumber());
        order.setRecipientAddress(dto.recipientAddress());

        Set<OrderItem> orderItems = createOrderItems(order, dto);
        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        cartService.clearCart(userUuid);

        OrderResponseDTO response = orderMapper.toOrderResponseDTO(savedOrder);

        return response;
    }

    // Get all orders by user uuid
    public PaginatedResponse<OrderResponseDTO> getUserOrders(UUID userUuid, Pageable pageable) {
        Page<Order> orderPage = orderRepository.findByUserUuid(userUuid, pageable);

        List<OrderResponseDTO> orderDTOs = orderMapper.toOrderResponseDTOs(orderPage.getContent());

        return new PaginatedResponse<>(
                orderDTOs,
                orderPage.getNumber(),
                orderPage.getSize(),
                orderPage.getTotalElements(),
                orderPage.getTotalPages());
    }

    // Get order by uuid
    public OrderResponseDTO getOrderByUuid(UUID uuid) {
        Order order = orderRepository.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with uuid: " + uuid));

        OrderResponseDTO response = orderMapper.toOrderResponseDTO(order);

        return response;
    }

    // --------- OrderItem ---------

    private Set<OrderItem> createOrderItems(Order order, OrderCreateRequestDTO dto) {
        Set<OrderItem> orderItems = new HashSet<>();

        dto.items().forEach(item -> {
            Product product = productService.recordSale(item.productUuid(), item.quantity());

            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setQuantity(item.quantity());
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setDiscountPercentage(product.getDiscountPercentage());
            orderItem.setImageUrl(product.getImageUrl());

            orderItems.add(orderItem);
        });

        return orderItems;
    }

}
