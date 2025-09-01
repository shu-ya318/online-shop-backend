package com.project.demo.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OAuth2CodeRequestDTO {
    @NotBlank(message = "OAuth2 code cannot be blank")
    private String oauth2Code;
}
