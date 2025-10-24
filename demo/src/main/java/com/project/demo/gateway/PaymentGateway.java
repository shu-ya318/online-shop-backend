package com.project.demo.gateway;

import com.project.demo.dto.gateway.PaymentGatewayRequestDTO;
import com.project.demo.dto.gateway.PaymentGatewayResponseDTO;
import com.project.demo.enumeration.PaymentMethod;
import com.project.demo.model.Payment;

public interface PaymentGateway {
    PaymentGatewayResponseDTO createPayment(Payment payment);

    PaymentGatewayResponseDTO capturePayment(PaymentGatewayRequestDTO requestDTO);

    PaymentMethod getPaymentMethod();
}
