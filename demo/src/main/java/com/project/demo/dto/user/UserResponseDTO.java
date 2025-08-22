package com.project.demo.dto.user;

public record UserResponseDTO(
	String uuid, 
	String email, 
	String name, 
	String phoneNumber,
	String birth,
	String address) {
}
