package com.project.demo.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;

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
import com.project.demo.enumeration.PaymentStatus;
import com.project.demo.exception.EntityNotFoundException;
import com.project.demo.dto.order.OrderCreateRequestDTO;
import com.project.demo.repository.OrderRepository;
import com.project.demo.repository.PaymentRepository;
import com.project.demo.repository.UserRepository;
import com.project.demo.mapper.OrderMapper;
import com.project.demo.mapper.util.PriceCalculationUtils;
import com.project.demo.model.Payment;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
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
        order.setExpiredAt(LocalDateTime.now().plusMinutes(10));
        order.setRecipientName(dto.recipientName());
        order.setRecipientPhoneNumber(dto.recipientPhoneNumber());
        order.setRecipientAddress(dto.recipientAddress());

        Set<OrderItem> orderItems = createOrderItems(order, dto);
        order.setItems(orderItems);

        order.setTotal(PriceCalculationUtils.calculateTotal(orderItems));

        Order savedOrder = orderRepository.save(order);

        cartService.clearCart(userUuid);

        OrderResponseDTO responseDTO = orderMapper.toOrderResponseDTO(savedOrder);

        return responseDTO;
    }

    // Get all orders by user uuid
    public PaginatedResponse<OrderResponseDTO> getOrders(UUID userUuid, Pageable pageable) {
        Page<Order> orderPage = orderRepository.findByUserUuid(userUuid, pageable);

        List<OrderResponseDTO> orderDTO = orderMapper.toOrderResponseDTOs(orderPage.getContent());


        PaginatedResponse<OrderResponseDTO> responseDTO = new PaginatedResponse<>(
                orderDTO,
                orderPage.getNumber(),
            orderPage.getSize(),
            orderPage.getTotalElements(),
            orderPage.getTotalPages());

        return responseDTO;
    }

    // Get order by uuid
    public OrderResponseDTO getOrderByUuid(UUID uuid) {
        Order order = orderRepository.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with uuid: " + uuid));

        OrderResponseDTO responseDTO = orderMapper.toOrderResponseDTO(order);

        return responseDTO;
    }

    // Cancel order by uuid
    @Transactional
    public OrderResponseDTO cancelOrderByUuid(UUID orderUuid) {
        Order order = orderRepository.findByUuid(orderUuid)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with uuid: " + orderUuid));

        if (order.getStatus() == OrderStatus.PENDING) {
            order.setStatus(OrderStatus.CANCELLED);

            List<Payment> payments = paymentRepository.findByOrderUuid(orderUuid);
            payments.forEach(payment -> payment.setStatus(PaymentStatus.CANCELLED));
            paymentRepository.saveAll(payments);

            productService.releaseStockForOrder(order);

            orderRepository.save(order);
        }

        OrderResponseDTO responseDTO = orderMapper.toOrderResponseDTO(order);

        return responseDTO;
    }

    // Scheduled task to cancel expired orders
    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    public void cancelExpiredOrders() {
        List<Order> expiredOrders = orderRepository.findByStatusAndExpiredAtLessThan(OrderStatus.PENDING,
                LocalDateTime.now());
        for (Order order : expiredOrders) {
            order.setStatus(OrderStatus.CANCELLED);

            productService.releaseStockForOrder(order);
        }
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
