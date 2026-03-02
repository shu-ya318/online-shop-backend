package com.project.demo.dto.user;

import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;

public record UserResponseDTO(
	@Schema(description = "User unique identifier", example = "550e8400-e29b-41d4-a716-446655440000")
	UUID uuid, 
	@Schema(description = "User email address", example = "john.doe@example.com")
	String email, 
	@Schema(description = "User full name", example = "John Doe")
	String name, 
	@Schema(description = "10-digit phone number", example = "0912345678")
	String phoneNumber,
	@Schema(description = "Birth date", example = "1990-01-01")
	String birth,
	@Schema(description = "Mailing address", example = "123 Main St, Springfield")
	String address) {
}
