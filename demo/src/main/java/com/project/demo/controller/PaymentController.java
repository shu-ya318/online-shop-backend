package com.project.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.demo.dto.gateway.PaymentGatewayRequestDTO;
import com.project.demo.dto.payment.PaymentCaptureResponseDTO;
import com.project.demo.dto.payment.PaymentRequestDTO;
import com.project.demo.dto.payment.PaymentResponseDTO;
import com.project.demo.model.User;
import com.project.demo.service.PaymentService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import static com.project.demo.data.PathConstantData.API_CURRENT_USER_PAYMENTS;
import static com.project.demo.data.PathConstantData.API_CURRENT_USER_PAYMENTS_CAPTURE;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Value("${frontend.url}")
    private String frontendUrl;

    /*
     * POST method
     */

    @PostMapping(API_CURRENT_USER_PAYMENTS)
    public ResponseEntity<PaymentResponseDTO> createPayment(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody PaymentRequestDTO dto) {
        PaymentResponseDTO responseDTO = paymentService.createPayment(dto);

        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping(API_CURRENT_USER_PAYMENTS_CAPTURE)
    public ResponseEntity<PaymentCaptureResponseDTO> capturePayment(@Valid @RequestBody PaymentGatewayRequestDTO requestDTO) {
        PaymentCaptureResponseDTO responseDTO = paymentService.capturePayment(requestDTO);
        
        return ResponseEntity.ok(responseDTO);
    }
}
