package com.project.demo.dto.user;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public record UserRegisterRequestDTO(
	@NotBlank(message = "Email is required!") 
	@Email(message = "Invalid email format!") 
	String email,

	@NotBlank(message = "Password is required!") 
	@Size(min = 8, max = 20,  message = "Password must be 8-20 characters long!") 
	String password, // Unencrypted

	@NotBlank(message = "Phone number is required!") 
	@Size(min = 10, max = 10, message = "Phone number must be 10 digits!")
	String phoneNumber,

	@NotBlank(message = "Name is required!") 
	String name,

	@NotNull(message = "Birth is required!") 
	@Past(message = "Birth date must be in the past!")
	@JsonFormat(pattern = "yyyy-MM-dd") 
	LocalDate birth,

	@NotBlank(message = "Address is required!")
	String address){
}
