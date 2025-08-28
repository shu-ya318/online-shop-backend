package com.project.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.project.demo.dto.user.UserLoginRequestDTO;
import com.project.demo.dto.user.UserLoginResponseDTO;
import com.project.demo.dto.user.UserPasswordUpdateRequestDTO;
import com.project.demo.dto.user.UserRegisterRequestDTO;
import com.project.demo.dto.user.UserUpdateRequestDTO;
import com.project.demo.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.Map;

import static com.project.demo.data.PathConstantData.API_VUE;
//import static com.project.demo.data.PathConstantData.API_PUBLIC;
import static com.project.demo.data.PathConstantData.API_REGISTER;
import static com.project.demo.data.PathConstantData.API_LOGIN;
import static com.project.demo.data.PathConstantData.API_CURRENT_USER;
import static com.project.demo.data.PathConstantData.API_UPDATE_USER;
import static com.project.demo.data.PathConstantData.API_UPDATE_PASSWORD;
import static com.project.demo.data.PathConstantData.API_REFRESH_TOKEN;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = API_VUE)
// @RequestMapping(API_PUBLIC)
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
            UserLoginResponseDTO responseDTO = userService.login(dto);

            return ResponseEntity.ok(responseDTO);
        } catch (RuntimeException e) {
            if ("Invalid email or password".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", e.getMessage()));
            } else if ("User is deleted".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", e.getMessage()));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
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
            System.out.println("Refresh token failed: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Refresh token failed"));
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
            return ResponseEntity.ok(userService.getUserResponseDTOByEmail(principal.getName()));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
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
            @Valid @RequestBody UserUpdateRequestDTO user) {
        try {
            return ResponseEntity.ok(userService.updateUser(principal.getName(), user));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An unexpected error occurred while updating user profile."));
        }
    }

    @PutMapping(API_UPDATE_PASSWORD)
    public ResponseEntity<?> updatePassword(Principal principal,
            @Valid @RequestBody UserPasswordUpdateRequestDTO passwordUpdateDTO) {
        try {
            userService.updatePassword(principal.getName(), passwordUpdateDTO);

            return ResponseEntity.ok(Map.of("message", "Password updated successfully!"));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An unexpected error occurred."));
        }
    }
}
