package com.project.demo.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

public record UserPasswordUpdateRequestDTO(
		@NotBlank(message = "Password is required!") 
		@Size(min = 8, max = 20,  message = "Password must be 8-20 characters long!") 
		@Schema(description = "Old password unencrypted", example = "oldpassword123")
		String oldPassword, // Unencrypted
		
		@NotBlank(message = "Password is required!") 
		@Size(min = 8, max = 20,  message = "Password must be 8-20 characters long!") 
		@Schema(description = "New password unencrypted", example = "newpassword123")
		String newPassword // Unencrypted 
	) {
}
