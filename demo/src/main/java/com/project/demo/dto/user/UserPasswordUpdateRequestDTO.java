package com.project.demo.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserPasswordUpdateRequestDTO(
		@NotBlank(message = "Password is required!") 
		@Size(min = 8, max = 20,  message = "Password must be 8-20 characters long!") 
		String oldPassword, // Unencrypted
		
		@NotBlank(message = "Password is required!") 
		@Size(min = 8, max = 20,  message = "Password must be 8-20 characters long!") 
		String newPassword // Unencrypted 
	) {
}
