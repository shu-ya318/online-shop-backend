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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.Parameter;

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
@Tag(name = "User")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    // ---------- AUTH ----------

    /*
     * POST method
     */

    @Operation(summary = "Register user", description = "Registers a new user account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = Map.class),
                    examples = @ExampleObject(value = "{\"message\":\"Register successfully!\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":400,\"error\":\"Bad Request\",\"message\":\"Invalid email format\"}"))),
            @ApiResponse(responseCode = "409", description = "Email already exists", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":409,\"error\":\"Conflict\",\"message\":\"Email already exists\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"An unexpected error occurred!\"}")))
    })
    @PostMapping(API_REGISTER)
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody UserRegisterRequestDTO dto) {
        userService.register(dto);

        return ResponseEntity.ok(Map.of("message", "Register successfully!"));
    }

    @Operation(summary = "Login user", description = "Authenticates a user and returns an access token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successfully", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = TokenResponseDTO.class),
                    examples = @ExampleObject(value = "{\"accessToken\":\"string\",\"refreshToken\":\"string\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid credentials", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":400,\"error\":\"Bad Request\",\"message\":\"Invalid credentials\"}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Invalid email or password\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"An unexpected error occurred!\"}")))
    })
    @PostMapping(API_LOGIN)
    public ResponseEntity<TokenResponseDTO> login(@Valid @RequestBody UserLoginRequestDTO dto,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) {
        TokenResponseDTO responseDTO = userService.login(dto, request.getRemoteAddr());
        cookieUtil.addRefreshTokenCookie(response, responseDTO.refreshToken());

        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Exchange OAuth2 code", description = "Exchanges an OAuth2 code for an access token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Code exchanged successfully", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = TokenResponseDTO.class),
                    examples = @ExampleObject(value = "{\"accessToken\":\"string\",\"refreshToken\":\"string\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid code", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":400,\"error\":\"Bad Request\",\"message\":\"Invalid OAuth2 code\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"An unexpected error occurred!\"}")))
    })
    @PostMapping(API_OAUTH2_EXCHANGE_CODE)
    public ResponseEntity<TokenResponseDTO> exchangeOauth2Code(@Valid @RequestBody Oauth2CodeRequestDTO dto,
                                                                     HttpServletResponse response) {
        TokenResponseDTO responseDTO = userService.exchangeOauth2Code(dto.oauth2Code());
        cookieUtil.addRefreshTokenCookie(response, responseDTO.refreshToken());

        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Refresh tokens", description = "Refreshes the access token using a valid refresh token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tokens refreshed successfully", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = TokenResponseDTO.class),
                    examples = @ExampleObject(value = "{\"accessToken\":\"string\",\"refreshToken\":\"string\"}"))),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Refresh token not found in cookie!\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"An unexpected error occurred!\"}")))
    })
    @PostMapping(API_REFRESH_TOKENS)
    public ResponseEntity<TokenResponseDTO> refreshTokens(
            @Parameter(description = "Refresh token from cookie") @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidTokenException("Refresh token not found in cookie!");
        }

        TokenResponseDTO responseDTO = userService.refreshTokens(refreshToken);
        
        cookieUtil.addRefreshTokenCookie(response, responseDTO.refreshToken());
        
        return ResponseEntity.ok(responseDTO);
    }

    // ---------- CURRENT USER ----------

    /*
     * GET method
     */

    @Operation(summary = "Get current user", description = "Retrieves the profile of the currently authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile retrieved successfully", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = UserResponseDTO.class),
                    examples = @ExampleObject(value = "{\"uuid\":\"string\",\"email\":\"string\",\"name\":\"string\",\"phoneNumber\":\"string\",\"birth\":\"string\",\"address\":\"string\"}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Full authentication is required to access this resource\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"An unexpected error occurred!\"}")))
    })
    @GetMapping(API_CURRENT_USER)
    public ResponseEntity<UserResponseDTO> getCurrentUser(Principal principal) {
        UserResponseDTO responseDTO = userService.getUserByEmail(principal.getName());

        return ResponseEntity.ok(responseDTO);
    }

    /*
     * PUT method
     */

    @Operation(summary = "Update user profile", description = "Updates the profile information of the current user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = UserResponseDTO.class),
                    examples = @ExampleObject(value = "{\"uuid\":\"string\",\"email\":\"string\",\"name\":\"string\",\"phoneNumber\":\"string\",\"birth\":\"string\",\"address\":\"string\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":400,\"error\":\"Bad Request\",\"message\":\"Invalid phone number format\"}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Full authentication is required to access this resource\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"An unexpected error occurred!\"}")))
    })
    @PutMapping(API_CURRENT_USER_UPDATE_PROFILE)
    public ResponseEntity<UserResponseDTO> updateUserProfile(Principal principal,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Detailed user profile information to update", required = true,
                    content = @Content(schema = @Schema(implementation = UserUpdateRequestDTO.class)))
            @Valid @RequestBody UserUpdateRequestDTO dto) {
        UserResponseDTO responseDTO = userService.updateUserProfile(principal.getName(), dto);

        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Update user password", description = "Updates the password for the current user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password updated successfully", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = Map.class),
                    examples = @ExampleObject(value = "{\"message\":\"Password updated successfully!\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid old password/New passwords do not match", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":400,\"error\":\"Bad Request\",\"message\":\"Old password is incorrect\"}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Full authentication is required to access this resource\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"An unexpected error occurred!\"}")))
    })
    @PutMapping(API_CURRENT_USER_UPDATE_PASSWORD)
    public ResponseEntity<Map<String, String>> updateUserPassword(Principal principal,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Old and new password details", required = true,
                    content = @Content(schema = @Schema(implementation = UserPasswordUpdateRequestDTO.class)))
            @Valid @RequestBody UserPasswordUpdateRequestDTO dto,
            HttpServletResponse response) {
        userService.updateUserPassword(principal.getName(), dto);

        cookieUtil.clearRefreshTokenCookie(response);

        return ResponseEntity.ok(Map.of("message", "Password updated successfully!"));
    }
}
