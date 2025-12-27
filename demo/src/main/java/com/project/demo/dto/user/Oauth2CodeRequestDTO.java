package com.project.demo.dto.user;

import jakarta.validation.constraints.NotBlank;

public record Oauth2CodeRequestDTO(
    @NotBlank(message = "Oauth2 code is required!") 
    String oauth2Code) {
}
