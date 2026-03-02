package com.project.demo.dto.order;

import java.util.List;

import com.project.demo.enumeration.PaymentMethod;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

public record OrderCreateRequestDTO(
        @NotBlank(message = "Recipient name is required!")
        @Schema(description = "Recipient name", example = "John Doe")
        String recipientName,
        
        @NotBlank(message = "Recipient phone number is required!")
        @Size(min = 10, max = 10, message = "Phone number must be 10 digits!")
        @Schema(description = "10-digit phone number", example = "0911222333")
        String recipientPhoneNumber,
        
        @NotBlank(message = "Recipient address is required!")
        @Schema(description = "Recipient delivery address", example = "123 Delivery Rd, New York")
        String recipientAddress,
        
        @NotEmpty(message = "Order items cannot be empty!")
        @Valid
        List<OrderItemCreateRequestDTO> items,
        
        @Schema(description = "Additional order notes", example = "Please leave at the door.")
        String orderNotes,
        
        @NotNull(message = "Payment method is required!")
        PaymentMethod paymentMethod) {
}
