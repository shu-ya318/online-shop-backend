package com.project.demo.service;

import java.util.Set;
import java.time.LocalDateTime;

import com.project.demo.dto.user.UserRegisterRequestDTO;
import com.project.demo.dto.user.UserLoginRequestDTO;
import com.project.demo.dto.user.UserLoginResponseDTO;
import com.project.demo.dto.user.UserPasswordUpdateRequestDTO;
import com.project.demo.dto.user.UserUpdateRequestDTO;
import com.project.demo.dto.user.UserResponseDTO;
import com.project.demo.mapper.UserMapper;
import com.project.demo.enumeration.AccountStatus;
import com.project.demo.enumeration.AuthProvider;
import com.project.demo.enumeration.Role;

import com.project.demo.model.User;
import com.project.demo.repository.UserRepository;
import com.project.demo.security.JwtUtil;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final RedisService redisService;

    // Register
    @Transactional
    public void register(UserRegisterRequestDTO dto) {
        // 檢查 email 是否已註冊且未被刪除
        Optional<User> optionalUser = userRepository.findByEmail(dto.email());

        User user;
        
        if (optionalUser.isPresent()) {
            if (!optionalUser.get().isDeleted()) {
                System.out.println("Email already registered");
                throw new IllegalArgumentException("Email already registered");
            }

            // 軟刪除帳號再註冊：重新啟用帳號
            user = optionalUser.get();
            userMapper.updateUserFromUserRegisterRequestDTO(dto, user);
        } else {
            // 新使用者
            user = userMapper.toUser(dto);
        }

        user.setUuid(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setUserRoles(Set.of(Role.CUSTOMER));
        user.setAuthProvider(AuthProvider.LOCAL);
        user.setCreatedAt(LocalDateTime.now());
        user.setCreatedBy("system");
        
        userRepository.save(user);
    }

    // Login
    public UserLoginResponseDTO login(@RequestBody UserLoginRequestDTO dto) {
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (user.isDeleted()) {
            throw new RuntimeException("User is deleted");
        }

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        user.setLastLoginAt(LocalDateTime.now());
        user.setLastLoginIp(request.getRemoteAddr());
        user.setUpdatedAt(LocalDateTime.now());
        user.setUpdatedBy("system");

        userRepository.save(user);

        // 處理 Token，提供給客戶端
        var roles = user.getUserRoles().stream().map(Enum::name).collect(Collectors.toSet());
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUuid(), user.getEmail(), roles);

        String refreshToken = jwtUtil.generateRefreshToken(user.getUuid());

        redisService.saveRefreshToken(user.getUuid(), refreshToken, jwtUtil.getRefreshTokenExpiration(),
                java.util.concurrent.TimeUnit.MILLISECONDS);

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge((int) jwtUtil.getRefreshTokenExpiration() / 1000);
        response.addCookie(refreshCookie);

        UserLoginResponseDTO loginResponse = userMapper.toUserLoginResponseDTO(accessToken);
        return loginResponse;
    }

    // Refresh Token
    @Transactional
    public String refreshToken() {
        String refreshToken = null;

        if (request.getCookies() != null) {
            refreshToken = Arrays.stream(request.getCookies())
                    .filter(c -> "refreshToken".equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }

        if (refreshToken == null) {
            throw new RuntimeException("Refresh token not found in cookie");
        }

        String uuid;
        try {
            uuid = jwtUtil.getUuidFromRefreshToken(refreshToken);
        } catch (Exception e) {
            throw new RuntimeException("Invalid refresh token format");
        }

        if (!jwtUtil.validateRefreshToken(uuid, refreshToken)) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isDeleted()) {
            throw new RuntimeException("User account is deleted");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUuid(), user.getEmail(),
                user.getUserRoles().stream().map(Enum::name).collect(Collectors.toSet()));
        return accessToken;
    }



    // Get Current User Info
    public UserResponseDTO getUserResponseDTOByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return userMapper.toResponseDto(user);
    }

    // Update User profile
    @Transactional
    public UserResponseDTO updateUser(String email, UserUpdateRequestDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        userMapper.updateUserFromUserUpdateRequestDTO(dto, user);
        userRepository.save(user);

        return userMapper.toResponseDto(user);
    }

    @Transactional
    public void updatePassword(String email, UserPasswordUpdateRequestDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if (!passwordEncoder.matches(dto.oldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Incorrect old password");
        }

        String encodedPassword = passwordEncoder.encode(dto.newPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }
}
