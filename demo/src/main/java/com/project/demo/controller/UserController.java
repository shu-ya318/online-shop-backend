package com.project.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.demo.dto.user.OAuth2CodeRequestDTO;
import com.project.demo.dto.user.UserLoginRequestDTO;
import com.project.demo.dto.user.UserLoginResponseDTO;
import com.project.demo.dto.user.UserPasswordUpdateRequestDTO;
import com.project.demo.dto.user.UserRegisterRequestDTO;
import com.project.demo.dto.user.UserResponseDTO;
import com.project.demo.dto.user.UserUpdateRequestDTO;
import com.project.demo.service.UserService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.Map;

import static com.project.demo.data.PathConstantData.API_REGISTER;
import static com.project.demo.data.PathConstantData.API_LOGIN;
import static com.project.demo.data.PathConstantData.API_CURRENT_USER;
import static com.project.demo.data.PathConstantData.API_UPDATE_USER;
import static com.project.demo.data.PathConstantData.API_UPDATE_PASSWORD;
import static com.project.demo.data.PathConstantData.API_REFRESH_TOKEN;
import static com.project.demo.data.PathConstantData.API_OAUTH2_EXCHANGE_CODE;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /*
     * POST method
     */

    @PostMapping(API_REGISTER)
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody UserRegisterRequestDTO dto) {
        userService.register(dto);

        return ResponseEntity.ok(Map.of("message", "Register successfully!"));
    }

    @PostMapping(API_LOGIN)
    public ResponseEntity<UserLoginResponseDTO> login(@Valid @RequestBody UserLoginRequestDTO dto) {
        UserLoginResponseDTO responseDTO = userService.login(dto);

        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping(API_REFRESH_TOKEN)
    public ResponseEntity<Map<String, String>> refreshToken() {
        String accessToken = userService.refreshToken();

        return ResponseEntity.ok(Map.of("accessToken", accessToken));
    }

    @PostMapping(API_OAUTH2_EXCHANGE_CODE)
    public ResponseEntity<UserLoginResponseDTO> exchangeOAuth2Code(@Valid @RequestBody OAuth2CodeRequestDTO dto) {
        UserLoginResponseDTO responseDTO = userService.exchangeOAuth2Code(dto.oauth2Code());

        return ResponseEntity.ok(responseDTO);
    }

    /*
     * GET method
     */

    @GetMapping(API_CURRENT_USER)
    public ResponseEntity<UserResponseDTO> getCurrentUser(Principal principal) {
        UserResponseDTO responseDTO = userService.getUserResponseDTOByEmail(principal.getName());

        return ResponseEntity.ok(responseDTO);
    }

    /*
     * PUT method
     */

    @PutMapping(API_UPDATE_USER)
    public ResponseEntity<UserResponseDTO> updateUser(Principal principal,
            @Valid @RequestBody UserUpdateRequestDTO dto) {
        UserResponseDTO responseDTO = userService.updateUser(principal.getName(), dto);

        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping(API_UPDATE_PASSWORD)
    public ResponseEntity<Map<String, String>> updatePassword(Principal principal,
            @Valid @RequestBody UserPasswordUpdateRequestDTO dto) {
        userService.updatePassword(principal.getName(), dto);

        return ResponseEntity.ok(Map.of("message", "Password updated successfully!"));
    }
}
