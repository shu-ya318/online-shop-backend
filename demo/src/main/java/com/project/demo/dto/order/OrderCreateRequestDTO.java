package com.project.demo.dto.order;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record OrderCreateRequestDTO(
        @NotBlank(message = "Recipient name is required!")
        String recipientName,
        
        @NotBlank(message = "Recipient phone number is required!")
        @Size(min = 10, max = 10, message = "Phone number must be 10 digits!")
        String recipientPhoneNumber,
        
        @NotBlank(message = "Recipient address is required!")
        String recipientAddress,
        
        @NotEmpty(message = "Order items cannot be empty!")
        @Valid
        List<OrderItemCreateRequestDTO> items,
        
        
        String orderNotes) {
}
