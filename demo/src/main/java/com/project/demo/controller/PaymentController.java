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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;

import static com.project.demo.data.PathConstantData.API_CURRENT_USER_PAYMENTS;
import static com.project.demo.data.PathConstantData.API_CURRENT_USER_PAYMENTS_CAPTURE;

@RestController
@RequiredArgsConstructor
@Tag(name = "Payment")
public class PaymentController {

    private final PaymentService paymentService;

    @Value("${frontend.url}")
    private String frontendUrl;

    /*
     * POST method
     */

    @Operation(summary = "Create payment", description = "Initiates a payment process for an order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment initiated successfully", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = PaymentResponseDTO.class),
                    examples = @ExampleObject(value = "{\"uuid\":\"string\",\"transactionId\":\"string\",\"status\":\"COMPLETED\",\"method\":\"PAYPAL\",\"amount\":0.00,\"currency\":\"TWD\",\"orderUuid\":\"string\",\"redirectUrl\":\"string\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid payment request", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":400,\"error\":\"Bad Request\",\"message\":\"Invalid payment method\"}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Full authentication is required to access this resource\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"An unexpected error occurred!\"}")))
    })
    @PostMapping(API_CURRENT_USER_PAYMENTS)
    public ResponseEntity<PaymentResponseDTO> createPayment(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody PaymentRequestDTO requestDTO) {
        PaymentResponseDTO responseDTO = paymentService.createPayment(requestDTO);

        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Capture payment", description = "Captures a payment after authorization (Webhook/Callback).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment captured successfully", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = PaymentCaptureResponseDTO.class),
                    examples = @ExampleObject(value = "{\"status\":\"COMPLETED\",\"orderUuid\":\"string\"}"))),
            @ApiResponse(responseCode = "400", description = "Capture failed or invalid request", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":400,\"error\":\"Bad Request\",\"message\":\"Invalid capture code\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"An unexpected error occurred!\"}")))
    })
    @PostMapping(API_CURRENT_USER_PAYMENTS_CAPTURE)
    public ResponseEntity<PaymentCaptureResponseDTO> capturePayment(@Valid @RequestBody PaymentGatewayRequestDTO requestDTO) {
        PaymentCaptureResponseDTO responseDTO = paymentService.capturePayment(requestDTO);
        
        return ResponseEntity.ok(responseDTO);
    }
}
