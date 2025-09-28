package com.project.demo.gateway;

import com.project.demo.dto.gateway.PaymentGatewayRequestDTO;
import com.project.demo.dto.gateway.PaymentGatewayResponseDTO;
import com.project.demo.enumeration.PaymentMethod;
import com.project.demo.model.Payment;

public interface PaymentGateway {
    PaymentGatewayResponseDTO createPayment(Payment payment); // 處理 支付流程的細節邏輯

    PaymentGatewayResponseDTO capturePayment(PaymentGatewayRequestDTO requestDTO);

    PaymentMethod getPaymentMethod(); // 策略模式 能和工廠模式 協作的關鍵，回傳值通常是enum
}
