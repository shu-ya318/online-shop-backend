package com.project.demo.dto.user;

import jakarta.validation.constraints.NotBlank;

public record OAuth2CodeRequestDTO(
    @NotBlank(message = "OAuth2 code cannot be blank")
    String oauth2Code) {
}
