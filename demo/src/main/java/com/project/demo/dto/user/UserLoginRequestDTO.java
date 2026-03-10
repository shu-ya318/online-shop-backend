package com.project.demo.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

public record UserLoginRequestDTO(
	@NotBlank(message = "Email is required!") 
	@Email(message = "Invalid email format!") 
	@Schema(description = "User email address", example = "user@example.com")
	String email,

	@NotBlank(message = "Password is required!") 
	@Size(min = 8, max = 20,  message = "Password must be 8-20 characters long!") 
	@Schema(description = "User password", example = "password12345")
	String password) {
}
