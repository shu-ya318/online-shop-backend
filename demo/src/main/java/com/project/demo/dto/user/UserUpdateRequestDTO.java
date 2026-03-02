package com.project.demo.dto.user;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

public record UserUpdateRequestDTO(
	@NotBlank(message = "Phone number is required!") 
	@Size(min = 10, max = 10, message = "Phone number must be 10 digits!") 
	@Schema(description = "10-digit phone number", example = "0912345678")
	String phoneNumber,

	@NotBlank(message = "Name is required!") 
	@Schema(description = "User name", example = "John Doe")
	String name,

	@NotNull(message = "Birth is required!") 
	@Past(message = "Birth date must be in the past!")
	@JsonFormat(pattern = "yyyy-MM-dd") 
	@Schema(description = "Birth date", example = "2000-01-01")
	LocalDate birth,

	@NotBlank(message = "Address is required!") 
	@Schema(description = "Mailing address", example = "123 Main St, Springfield")
	String address) {
}
