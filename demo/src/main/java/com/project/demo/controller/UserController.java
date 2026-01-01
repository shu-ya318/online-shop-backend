package com.project.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.demo.dto.user.Oauth2CodeRequestDTO;
import com.project.demo.dto.user.UserLoginRequestDTO;

import com.project.demo.dto.user.TokenResponseDTO;
import com.project.demo.dto.user.UserPasswordUpdateRequestDTO;
import com.project.demo.dto.user.UserRegisterRequestDTO;
import com.project.demo.dto.user.UserResponseDTO;
import com.project.demo.dto.user.UserUpdateRequestDTO;
import com.project.demo.service.UserService;
import com.project.demo.security.JwtUtil;
import com.project.demo.security.CookieUtil;
import com.project.demo.exception.InvalidTokenException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.Map;

import static com.project.demo.data.PathConstantData.API_REGISTER;
import static com.project.demo.data.PathConstantData.API_LOGIN;
import static com.project.demo.data.PathConstantData.API_CURRENT_USER;
import static com.project.demo.data.PathConstantData.API_CURRENT_USER_UPDATE_PROFILE;
import static com.project.demo.data.PathConstantData.API_CURRENT_USER_UPDATE_PASSWORD;
import static com.project.demo.data.PathConstantData.API_REFRESH_TOKENS;
import static com.project.demo.data.PathConstantData.API_OAUTH2_EXCHANGE_CODE;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    // ---------- AUTH ----------

    /*
     * POST method
     */

    @PostMapping(API_REGISTER)
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody UserRegisterRequestDTO dto) {
        userService.register(dto);

        return ResponseEntity.ok(Map.of("message", "Register successfully!"));
    }

    @PostMapping(API_LOGIN)
    public ResponseEntity<String> login(@Valid @RequestBody UserLoginRequestDTO dto,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) {
        TokenResponseDTO responseDTO = userService.login(dto, request.getRemoteAddr());
        cookieUtil.addRefreshTokenCookie(response, responseDTO.refreshToken());

        return ResponseEntity.ok(responseDTO.accessToken());
    }

    @PostMapping(API_OAUTH2_EXCHANGE_CODE)
    public ResponseEntity<String> exchangeOauth2Code(@Valid @RequestBody Oauth2CodeRequestDTO dto,
                                                                     HttpServletResponse response) {
        TokenResponseDTO responseDTO = userService.exchangeOauth2Code(dto.oauth2Code());
        cookieUtil.addRefreshTokenCookie(response, responseDTO.refreshToken());

        return ResponseEntity.ok(responseDTO.accessToken());
    }

    @PostMapping(API_REFRESH_TOKENS)
    public ResponseEntity<String> refreshTokens(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidTokenException("Refresh token not found in cookie!");
        }

        TokenResponseDTO responseDTO = userService.refreshTokens(refreshToken);
        
        cookieUtil.addRefreshTokenCookie(response, responseDTO.refreshToken());
        
        return ResponseEntity.ok(responseDTO.accessToken());
    }

    // ---------- CURRENT USER ----------

    /*
     * GET method
     */

    @GetMapping(API_CURRENT_USER)
    public ResponseEntity<UserResponseDTO> getCurrentUser(Principal principal) {
        UserResponseDTO responseDTO = userService.getUserByEmail(principal.getName());

        return ResponseEntity.ok(responseDTO);
    }

    /*
     * PUT method
     */

    @PutMapping(API_CURRENT_USER_UPDATE_PROFILE)
    public ResponseEntity<UserResponseDTO> updateUserProfile(Principal principal,
            @Valid @RequestBody UserUpdateRequestDTO dto) {
        UserResponseDTO responseDTO = userService.updateUserProfile(principal.getName(), dto);

        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping(API_CURRENT_USER_UPDATE_PASSWORD)
    public ResponseEntity<Map<String, String>> updateUserPassword(Principal principal,
            @Valid @RequestBody UserPasswordUpdateRequestDTO dto,
            HttpServletResponse response) {
        userService.updateUserPassword(principal.getName(), dto);

        cookieUtil.clearRefreshTokenCookie(response);

        return ResponseEntity.ok(Map.of("message", "Password updated successfully!"));
    }
}
