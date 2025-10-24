package com.project.demo.service;

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
import com.project.demo.exception.UserAlreadyExistsException;
import com.project.demo.exception.InvalidCredentialsException;
import com.project.demo.exception.UserDeletedException;
import com.project.demo.exception.InvalidTokenException;
import com.project.demo.exception.IncorrectPasswordException;
import com.project.demo.exception.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        Optional<User> optionalUser = userRepository.findByEmail(dto.email());

        User user;

        if (optionalUser.isPresent()) {
            if (!optionalUser.get().isDeleted()) {
                throw new UserAlreadyExistsException("Email already registered!");
            }

            user = optionalUser.get();
            userMapper.updateUserFromUserRegisterRequestDTO(dto, user);
        } else {
            user = userMapper.toUser(dto);
        }

        user.setUuid(UUID.randomUUID());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setUserRoles(Set.of(Role.CUSTOMER));
        user.setAuthProvider(AuthProvider.LOCAL);
        user.setCreatedAt(LocalDateTime.now());
        user.setCreatedBy("system");

        userRepository.save(user);
    }

    // Login
    public UserLoginResponseDTO login(UserLoginRequestDTO dto) {
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password!"));

        if (user.isDeleted()) {
            throw new UserDeletedException("User is deleted!");
        }

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password!");
        }

        user.setLastLoginAt(LocalDateTime.now());
        user.setLastLoginIp(request.getRemoteAddr());
        user.setUpdatedAt(LocalDateTime.now());
        user.setUpdatedBy("system");

        userRepository.save(user);

        // Handle token, provide to client
        var roles = user.getUserRoles().stream().map(Enum::name).collect(Collectors.toSet());

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUuid(), user.getEmail(),
                roles);
        String refreshToken = jwtUtil.generateRefreshToken(user.getUuid());

        redisService.saveRefreshToken(user.getUuid().toString(), refreshToken, jwtUtil.getRefreshTokenExpiration(),
                java.util.concurrent.TimeUnit.MILLISECONDS);

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);

        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge((int) jwtUtil.getRefreshTokenExpiration() / 1000);

        response.addCookie(refreshCookie);

        UserLoginResponseDTO loginResponseDTO = userMapper.toUserLoginResponseDTO(accessToken);

        return loginResponseDTO;
    }

    // OAuth2 Authorization Code Exchange
    @Transactional
    public UserLoginResponseDTO exchangeOAuth2Code(String oauth2Code) {
        String redisOAuth2Code = redisService.getOAuth2AuthCode(oauth2Code);

        if (redisOAuth2Code == null) {
            throw new InvalidTokenException("oauth2 code not found in redis!");
        }

        User user = userRepository.findByUuid(UUID.fromString(redisOAuth2Code))
                .orElseThrow(() -> new EntityNotFoundException("User not found!"));

        if (user.isDeleted()) {
            throw new UserDeletedException("User account is deleted!");
        }

        var roles = user.getUserRoles().stream().map(Enum::name).collect(Collectors.toSet());

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUuid(), user.getEmail(), roles);
        String refreshToken = jwtUtil.generateRefreshToken(user.getUuid());

        redisService.saveRefreshToken(user.getUuid().toString(), refreshToken, jwtUtil.getRefreshTokenExpiration(),
                java.util.concurrent.TimeUnit.MILLISECONDS);

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);

        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge((int) jwtUtil.getRefreshTokenExpiration() / 1000);

        response.addCookie(refreshCookie);

        UserLoginResponseDTO responseDTO = userMapper.toUserLoginResponseDTO(accessToken);
        return responseDTO;
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
            throw new InvalidTokenException("Refresh token not found in cookie!");
        }

        String uuid;

        try {
            uuid = jwtUtil.getUuidFromRefreshToken(refreshToken);
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid refresh token format!");
        }

        if (!jwtUtil.validateRefreshToken(UUID.fromString(uuid), refreshToken)) {
            throw new InvalidTokenException("Invalid or expired refresh token!");
        }

        User user = userRepository.findByUuid(UUID.fromString(uuid))
                .orElseThrow(() -> new EntityNotFoundException("User not found!"));

        if (user.isDeleted()) {
            throw new UserDeletedException("User account is deleted!");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUuid(), user.getEmail(),
                user.getUserRoles().stream().map(Enum::name).collect(Collectors.toSet()));

        return accessToken;
    }

    // Get Current User Info
    public UserResponseDTO getUserResponseDTOByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        UserResponseDTO responseDTO = userMapper.toResponseDto(user);

        return responseDTO;
    }

    // Update User profile
    @Transactional
    public UserResponseDTO updateUser(String email, UserUpdateRequestDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        userMapper.updateUserFromUserUpdateRequestDTO(dto, user);
        userRepository.save(user);

        UserResponseDTO responseDTO = userMapper.toResponseDto(user);

        return responseDTO;
    }

    // Update password
    @Transactional
    public void updatePassword(String email, UserPasswordUpdateRequestDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if (!passwordEncoder.matches(dto.oldPassword(), user.getPassword())) {
            throw new IncorrectPasswordException("Incorrect old password!");
        }

        String encodedPassword = passwordEncoder.encode(dto.newPassword());

        user.setPassword(encodedPassword);

        userRepository.save(user);
    }
}
