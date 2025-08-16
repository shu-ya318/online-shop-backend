package com.project.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.project.demo.security.JwtUtil;
import com.project.demo.model.User;
import com.project.demo.dto.user.UserLoginRequestDTO;
import com.project.demo.dto.user.UserRegisterRequestDTO;
import com.project.demo.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;

import static com.project.demo.data.PathConstantData.API_VUE;
import static com.project.demo.data.PathConstantData.API_PUBLIC;
import static com.project.demo.data.PathConstantData.API_REGISTER;
import static com.project.demo.data.PathConstantData.API_LOGIN;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = API_VUE)
@RequestMapping(API_PUBLIC)
public class UserController {

	private final UserService userService;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

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

   @PostMapping(API_LOGIN)
   public ResponseEntity<?> login(@Valid @RequestBody UserLoginRequestDTO dto) {
       try {
           if (dto == null) {
               throw new IllegalArgumentException("Login data cannot be empty");
           }
           Optional<User> optionalUser = userService.getUserByEmail(dto.email());

           if (optionalUser.isEmpty()) {
               return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid email or password"));
           }

		User user = optionalUser.get();
		if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid email or password"));
		}

           String jwt = jwtUtil.generateToken(user.getEmail(), user.getUserRoles());
           
           return ResponseEntity.ok(Map.of("token", jwt));
       } catch (Exception e) {
           e.printStackTrace();
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", Map.of("message", "An unexpected error occurred. Please try again later.")));
       }
   }

}

