package com.project.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

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
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterRequestDTO dto) {
        try {
            userService.register(dto);

            return ResponseEntity.ok(Map.of("message", "Register successfully!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An unexpected error occurred. Please try again later!"));
        }
    }

    @PostMapping(API_LOGIN)
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginRequestDTO dto) {
        try {
            UserLoginResponseDTO response = userService.login(dto);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if ("User is deleted".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", e.getMessage()));
            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", e.getMessage()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An unexpected error occurred. Please try again later!"));
        }
    }

    @PostMapping(API_REFRESH_TOKEN)
    public ResponseEntity<?> refreshToken() {
        try {
            String accessToken = userService.refreshToken();

            return ResponseEntity.ok(Map.of("accessToken", accessToken));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An unexpected error occurred. Please try again later!"));
        }
    }

    @PostMapping(API_OAUTH2_EXCHANGE_CODE)
    public ResponseEntity<?> exchangeOAuth2Code(@Valid @RequestBody OAuth2CodeRequestDTO dto) {
        try {
            UserLoginResponseDTO response = userService.exchangeOAuth2Code(dto.getOauth2Code());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if ("oauth2 code not found in redis".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", e.getMessage()));
            } else if ("User not found".equals(e.getMessage())) {
                return ResponseEntity.notFound()
                        .build();
            } else if ("User account is deleted".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", e.getMessage()));
            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", e.getMessage()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An unexpected error occurred. Please try again later!"));
        }
    }

    /*
     * GET method
     */
    @GetMapping(API_CURRENT_USER)
    public ResponseEntity<?> getCurrentUser(Principal principal) {
        try {
            UserResponseDTO response = userService.getUserResponseDTOByEmail(principal.getName());

            return ResponseEntity.ok(response);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.notFound()
                    .build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An unexpected error occurred. Please try again later!"));
        }
    }

    /*
     * PUT
     */
    @PutMapping(API_UPDATE_USER)
    public ResponseEntity<?> updateUser(Principal principal,
            @Valid @RequestBody UserUpdateRequestDTO dto) {
        try {
            UserResponseDTO response = userService.updateUser(principal.getName(), dto);

            return ResponseEntity.ok(response);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.notFound()
                    .build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An unexpected error occurred while updating user profile."));
        }
    }

    @PutMapping(API_UPDATE_PASSWORD)
    public ResponseEntity<?> updatePassword(Principal principal,
            @Valid @RequestBody UserPasswordUpdateRequestDTO dto) {
        try {
            userService.updatePassword(principal.getName(), dto);

            return ResponseEntity.ok(Map.of("message", "Password updated successfully!"));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.notFound()
                    .build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An unexpected error occurred."));
        }
    }
}
