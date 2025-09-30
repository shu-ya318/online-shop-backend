package com.project.demo.gateway;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.project.demo.dto.gateway.PaymentGatewayRequestDTO;
import com.project.demo.dto.gateway.PaymentGatewayResponseDTO;
import com.project.demo.enumeration.PaymentMethod;
import com.project.demo.enumeration.PaymentStatus;
import com.project.demo.model.Payment;

@Component
public class CashOnDeliveryGateway implements PaymentGateway {

    @Override
    public PaymentGatewayResponseDTO createPayment(Payment payment) {
        String transactionId = "cod-" + UUID.randomUUID().toString();
        
        return new PaymentGatewayResponseDTO(transactionId, PaymentStatus.PAY_ON_DELIVERY, "");
    }

    @Override
    public PaymentGatewayResponseDTO capturePayment(PaymentGatewayRequestDTO requestDTO) {
        return new PaymentGatewayResponseDTO(requestDTO.paymentId(), PaymentStatus.SUCCESS, "");
    }

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.CASH_ON_DELIVERY;
    }
}
