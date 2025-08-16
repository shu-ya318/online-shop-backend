package com.project.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.demo.dto.user.UserRegisterRequestDTO;
import com.project.demo.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Map;

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
	
    @PostMapping(API_REGISTER)
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterRequestDTO dto) {
        try {
            userService.register(dto);
            return ResponseEntity.ok("Registration successful!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", Map.of("message", "Email already registered")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", Map.of("message", "An unexpected error occurred. Please try again later.")));
        }
    }
    
//    @GetMapping(API_ACTIVATE)
//    public ResponseEntity<?> activate(@Valid @RequestParam String email, @RequestParam String code) {
//        userService.activate(email, code);
//        return ResponseEntity.ok("Account activated successfully.");
//    }

}

