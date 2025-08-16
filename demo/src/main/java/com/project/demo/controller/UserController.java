package com.project.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.demo.dto.user.UserRegisterRequestDTO;
import com.project.demo.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import static com.project.demo.data.PathConstantData.API_VUE;
import static com.project.demo.data.PathConstantData.API_PUBLIC;
import static com.project.demo.data.PathConstantData.API_REGISTER;
//import static com.project.demo.data.PathConstantData.API_ACTIVATE;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = API_VUE)
@RequestMapping(API_PUBLIC)
public class UserController {

	private final UserService userService;
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
    @PostMapping(API_REGISTER)
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterRequestDTO dto) {
        try {
            logger.info("Attempting to register user with email: {}", dto.email());
            userService.register(dto);
            logger.info("User registration successful for email: {}", dto.email());
            return ResponseEntity.ok("Registration successful, please check your email for activation.");
        } catch (IllegalArgumentException e) {
            logger.warn("Registration failed for email {}: {}", dto.email(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("An unexpected error occurred during registration for email: {}", dto.email(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred. Please try again later.");
        }
    }
    
//    @GetMapping(API_ACTIVATE)
//    public ResponseEntity<?> activate(@Valid @RequestParam String email, @RequestParam String code) {
//        userService.activate(email, code);
//        return ResponseEntity.ok("Account activated successfully.");
//    }

}

