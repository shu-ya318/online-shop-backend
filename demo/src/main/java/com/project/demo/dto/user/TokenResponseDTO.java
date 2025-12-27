package com.project.demo.dto.user;

public record TokenResponseDTO(
    String accessToken, 
    String refreshToken) {
}
