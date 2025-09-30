package com.project.demo.dto.user;

import jakarta.validation.constraints.NotBlank;

public record OAuth2CodeRequestDTO(
    @NotBlank(message = "OAuth2 code is required!")
    String oauth2Code) {
}
