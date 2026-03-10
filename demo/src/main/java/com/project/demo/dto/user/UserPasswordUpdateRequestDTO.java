package com.project.demo.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

public record UserPasswordUpdateRequestDTO(
		@NotBlank(message = "Password is required!") 
		@Size(min = 8, max = 20,  message = "Password must be 8-20 characters long!") 
		@Schema(description = "Old password unencrypted", example = "password12345")
		String oldPassword, // Unencrypted
		
		@NotBlank(message = "Password is required!") 
		@Size(min = 8, max = 20,  message = "Password must be 8-20 characters long!") 
		@Schema(description = "New password unencrypted", example = "newpassword12345")
		String newPassword // Unencrypted 
	) {
}
