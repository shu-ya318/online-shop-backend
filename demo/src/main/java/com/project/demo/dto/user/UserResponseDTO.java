package com.project.demo.dto.user;

import java.util.UUID;

public record UserResponseDTO(
	UUID uuid, 
	String email, 
	String name, 
	String phoneNumber,
	String birth,
	String address) {
}
