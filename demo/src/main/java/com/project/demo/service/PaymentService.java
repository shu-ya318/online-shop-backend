package com.project.demo.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import com.project.demo.repository.OrderRepository;
import com.project.demo.repository.PaymentRepository;

import jakarta.transaction.Transactional;

import com.project.demo.dto.gateway.PaymentGatewayResponseDTO;
import com.project.demo.dto.payment.PaymentRequestDTO;
import com.project.demo.dto.payment.PaymentResponseDTO;
import com.project.demo.dto.gateway.PaymentGatewayRequestDTO;
import com.project.demo.dto.payment.PaymentCaptureResponseDTO;
import com.project.demo.enumeration.OrderStatus;
import com.project.demo.enumeration.PaymentStatus;
import com.project.demo.exception.EntityNotFoundException;
import com.project.demo.exception.OperationNotSupportedException;
import com.project.demo.gateway.PaymentGateway;
import com.project.demo.gateway.PaymentGatewayFactory;
import com.project.demo.mapper.PaymentMapper;
import com.project.demo.model.Order;
import com.project.demo.model.Payment;

@Service
@RequiredArgsConstructor
public class PaymentService {

        private final PaymentGatewayFactory paymentGatewayFactory;
        private final OrderRepository orderRepository;
        private final PaymentRepository paymentRepository;
        private final PaymentMapper paymentMapper;
        private final OrderService orderService;

        // Create payment
        @Transactional
        public PaymentResponseDTO createPayment(PaymentRequestDTO dto) {
                Order order = orderRepository.findByUuid(dto.orderUuid())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Order not found with uuid: " + dto.orderUuid()));

                Payment payment = new Payment();

                payment.setUuid(UUID.randomUUID());
                payment.setOrder(order);
                payment.setAmount(order.getTotal());
                payment.setCurrency("TWD");
                payment.setCreatedAt(LocalDateTime.now());
                payment.setMethod(dto.method());

                // 依不同支付方式，有不同執行邏輯 (包含payment實現細節)
                PaymentGateway gateway = paymentGatewayFactory.getGateway(dto.method())
                                .orElseThrow(() -> new OperationNotSupportedException(
                                                "Payment method not supported: " + dto.method()));

                PaymentGatewayResponseDTO gatewayResponse = gateway.createPayment(payment);
                // 選擇支付方法，就寫入初始化的transactionId、status
                payment.setTransactionId(gatewayResponse.transactionId());
                payment.setStatus(gatewayResponse.status());

                paymentRepository.save(payment);

                // TODO: 移除，改由capturePayment來更新訂單狀態
                if (payment.getStatus() == PaymentStatus.PAY_ON_DELIVERY
                                || payment.getStatus() == PaymentStatus.SUCCESS) {
                        order.setStatus(OrderStatus.PROCESSING);
                        order.setUpdatedAt(LocalDateTime.now());

                        orderRepository.save(order);
                }

                PaymentResponseDTO response = paymentMapper.toPaymentResponseDTO(payment);

                return new PaymentResponseDTO(
                                response.uuid(),
                                response.transactionId(),
                                response.status(),
                                response.method(),
                                response.amount(),
                                response.currency(),
                                response.orderUuid(),
                                // 因應外部支付平台通常有redirectUrl，需要跳轉
                                gatewayResponse.redirectUrl());
        }

        // 因應外部支付平台通常需跳轉，再確認交易最終狀態
        @Transactional
        public PaymentCaptureResponseDTO capturePayment(PaymentGatewayRequestDTO dto) {
                Payment payment = paymentRepository.findByTransactionId(dto.paymentId())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Payment not found with transactionId: " + dto.paymentId()));

                PaymentGateway gateway = paymentGatewayFactory.getGateway(payment.getMethod())
                                .orElseThrow(() -> new OperationNotSupportedException(
                                                "Payment method not supported: " + payment.getMethod()));

                PaymentGatewayResponseDTO gatewayResponse = gateway.capturePayment(dto);

                payment.setStatus(gatewayResponse.status());
                payment.setTransactionId(gatewayResponse.transactionId());

                paymentRepository.save(payment);

                Order order = payment.getOrder();
                if (order == null) {
                        throw new EntityNotFoundException(
                                        "Order not found for payment transactionId: " + dto.paymentId());
                }

                if (gatewayResponse.status() == PaymentStatus.SUCCESS
                                || gatewayResponse.status() == PaymentStatus.PAY_ON_DELIVERY) {
                        order.setStatus(OrderStatus.PROCESSING);
                        order.setUpdatedAt(LocalDateTime.now());

                        orderRepository.save(order);
                } else {
                        orderService.cancelOrderByUuid(order.getUuid());
                }

                return new PaymentCaptureResponseDTO(payment.getStatus(), order.getUuid());
        }
}
