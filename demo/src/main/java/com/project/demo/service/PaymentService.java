package com.project.demo.service;

import java.time.LocalDateTime;
import java.util.List;
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

                // Depending on different payment methods, there are different execution logic (including payment implementation details)
                PaymentGateway gateway = paymentGatewayFactory.getGateway(dto.method())
                                .orElseThrow(() -> new OperationNotSupportedException(
                                                "Payment method not supported: " + dto.method()));

                PaymentGatewayResponseDTO gatewayResponseDTO = gateway.createPayment(payment);
                // Choose payment method, write the initialized transactionId and status
                payment.setTransactionId(gatewayResponseDTO.transactionId());
                payment.setStatus(gatewayResponseDTO.status());

                // For cash on delivery, directly update the order status (no external confirmation required)
                if (payment.getStatus() == PaymentStatus.PAY_ON_DELIVERY) {
                        order.setStatus(OrderStatus.PROCESSING);
                        order.setUpdatedAt(LocalDateTime.now());
                        orderRepository.save(order);
                }

                Payment savedPayment = paymentRepository.save(payment);
                PaymentResponseDTO baseResponse = paymentMapper.toPaymentResponseDTO(savedPayment);

                return new PaymentResponseDTO(
                                baseResponse.uuid(),
                                baseResponse.transactionId(),
                                baseResponse.status(),
                                baseResponse.method(),
                                baseResponse.amount(),
                                baseResponse.currency(),
                                baseResponse.orderUuid(),
                                // For external payment platforms
                                gatewayResponseDTO.redirectUrl());
        }

        // For external payment platforms, usually need to redirect, then confirm the final transaction status
        @Transactional
        public PaymentCaptureResponseDTO capturePayment(PaymentGatewayRequestDTO dto) {
                Payment payment = paymentRepository.findByTransactionId(dto.paymentId())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Payment not found with transactionId: " + dto.paymentId()));

                PaymentGateway gateway = paymentGatewayFactory.getGateway(payment.getMethod())
                                .orElseThrow(() -> new OperationNotSupportedException(
                                                "Payment method not supported: " + payment.getMethod()));

                PaymentGatewayResponseDTO gatewayResponseDTO = gateway.capturePayment(dto);

                payment.setStatus(gatewayResponseDTO.status());
                payment.setTransactionId(gatewayResponseDTO.transactionId());

                Order order = payment.getOrder();

                if (order == null) {
                        throw new EntityNotFoundException(
                        "Order not found for payment transactionId: " + dto.paymentId());
                }

                // For external payment platforms, update the order status based on the final payment result
                if (gatewayResponseDTO.status() == PaymentStatus.SUCCESS) {
                        order.setStatus(OrderStatus.PROCESSING);
                        order.setUpdatedAt(LocalDateTime.now());

                        orderRepository.save(order);
                } else {
                        // If payment fails or is cancelled, cancel the order
                        order.setStatus(OrderStatus.CANCELLED);

                        orderRepository.save(order);
                }

                paymentRepository.save(payment);

                return new PaymentCaptureResponseDTO(payment.getStatus(), order.getUuid());
        }

        // Cancel payments for order
        @Transactional
        public void cancelPaymentsForOrder(UUID orderUuid) {
                List<Payment> payments = paymentRepository.findByOrderUuid(orderUuid);

                if (payments.isEmpty()) {
                        return;
                }

                payments.forEach(payment -> payment.setStatus(PaymentStatus.CANCELLED));

                paymentRepository.saveAll(payments);
        }
}
