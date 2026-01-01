package com.project.demo.service;

import com.project.demo.dto.user.UserRegisterRequestDTO;
import com.project.demo.dto.user.UserLoginRequestDTO;
import com.project.demo.dto.user.TokenResponseDTO;
import com.project.demo.dto.user.UserPasswordUpdateRequestDTO;
import com.project.demo.dto.user.UserUpdateRequestDTO;
import com.project.demo.dto.user.UserResponseDTO;
import com.project.demo.mapper.UserMapper;
import com.project.demo.enumeration.AccountStatus;
import com.project.demo.enumeration.AuthProvider;
import com.project.demo.enumeration.Role;
import com.project.demo.enumeration.Modifier;
import com.project.demo.model.User;
import com.project.demo.repository.UserRepository;
import com.project.demo.security.JwtUtil;
import com.project.demo.exception.UserAlreadyExistsException;
import com.project.demo.exception.InvalidCredentialsException;
import com.project.demo.exception.UserDeletedException;
import com.project.demo.exception.InvalidTokenException;
import com.project.demo.exception.IncorrectPasswordException;
import com.project.demo.exception.EntityNotFoundException;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.ExpiredJwtException;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final RedisService redisService;

    // Register
    @Transactional
    public void register(UserRegisterRequestDTO dto) {
        Optional<User> optionalUser = userRepository.findByEmail(dto.email());

        User user;

        if (optionalUser.isPresent()) {
            user = optionalUser.get();
           
            if (!user.isDeleted()) {
                log.warn("Registration failed: Email {} is already registered and active.", dto.email());
                throw new UserAlreadyExistsException("Email already registered!");
            }

            userMapper.updateUserFromUserRegisterRequestDTO(dto, user);

            user.setDeleted(false);
            user.setDeletedAt(null);
            user.setUpdatedAt(LocalDateTime.now());
            user.setUpdatedBy(Modifier.SYSTEM);

        } else {
            user = userMapper.toUser(dto);

            user.setUuid(UUID.randomUUID());
            user.setCreatedAt(LocalDateTime.now());
            user.setCreatedBy(Modifier.SYSTEM);
        }

        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setUserRoles(Set.of(Role.CUSTOMER));
        user.setAuthProvider(AuthProvider.LOCAL);
        
        try {
            userRepository.save(user);
        } catch (Exception e) {
            log.error("Critical error saving user {}: {}", dto.email(), e.getMessage());
            throw e; 
        }
    }

    // Login
    @Transactional
    public TokenResponseDTO login(UserLoginRequestDTO dto, String ipAddress) {
        try {
            User user = userRepository.findByEmail(dto.email())
                    .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password!"));

            if (user.isDeleted()) {
                throw new UserDeletedException("User is deleted!");
            }

            if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
                throw new InvalidCredentialsException("Invalid email or password!");
            }

            user.setLastLoginAt(LocalDateTime.now());
            user.setLastLoginIp(ipAddress);
            user.setUpdatedAt(LocalDateTime.now());
            user.setUpdatedBy(Modifier.SYSTEM);

            Set<String> roles = user.getUserRoles().stream().map(Enum::name).collect(Collectors.toSet());
            String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUuid(), user.getEmail(),
                    roles);

            String refreshToken = jwtUtil.generateRefreshToken(user.getUuid());

            String refreshTokenJti = jwtUtil.getJtiFromToken(refreshToken);
            redisService.saveRefreshTokenJti(user.getUuid().toString(), refreshTokenJti, jwtUtil.getRefreshTokenExpiration(),
                    java.util.concurrent.TimeUnit.MILLISECONDS);

            userRepository.save(user);

            return new TokenResponseDTO(accessToken, refreshToken);
        } catch (InvalidCredentialsException | UserDeletedException e) {
            log.warn("Login rejection for {}: {}", dto.email(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected login failure for {}: {}", dto.email(), e.getMessage());
            throw e;
        }
    }

    // OAuth2 auth code exchange
    @Transactional
    public TokenResponseDTO exchangeOauth2Code(String oauth2Code) {
        String redisOauth2Code = redisService.getOauth2AuthCode(oauth2Code);

        if (redisOauth2Code == null) {
            throw new InvalidTokenException("Oauth2 auth code not found in redis!");
        }

        User user = userRepository.findByUuid(UUID.fromString(redisOauth2Code))
                .orElseThrow(() -> new EntityNotFoundException("User not found!"));

        if (user.isDeleted()) {
            throw new UserDeletedException("User account is deleted!");
        }

        Set<String> roles = user.getUserRoles().stream().map(Enum::name).collect(Collectors.toSet());
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUuid(), user.getEmail(), roles);
        
        String refreshToken = jwtUtil.generateRefreshToken(user.getUuid());

        String refreshTokenJti = jwtUtil.getJtiFromToken(refreshToken);
        redisService.saveRefreshTokenJti(user.getUuid().toString(), refreshTokenJti, jwtUtil.getRefreshTokenExpiration(),
                java.util.concurrent.TimeUnit.MILLISECONDS);

        return new TokenResponseDTO(accessToken, refreshToken);
    }

    // Refresh tokens (AccessToken and RefreshToken rotation)
    @Transactional
    public TokenResponseDTO refreshTokens(String refreshToken) {
        String uuid;
        String requestRefreshTokenJti;

        try {
            uuid = jwtUtil.getUuidFromRefreshToken(refreshToken);
            requestRefreshTokenJti = jwtUtil.getJtiFromToken(refreshToken);
        } catch (ExpiredJwtException e) {
            log.warn("Refresh token expired: {}", e.getMessage());
            throw new InvalidTokenException("Refresh token has expired!");
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid refresh token attempt: {}", e.getMessage());
            throw new InvalidTokenException("Invalid refresh token!");
        }

        String storedRefreshTokenJti = redisService.getRefreshTokenJti(uuid);

        if (storedRefreshTokenJti == null || !storedRefreshTokenJti.equals(requestRefreshTokenJti)) {
            log.error("Refresh token reuse detected or session expired for UUID: {}", uuid);
            throw new InvalidTokenException("Invalid refresh token!");
        }

        User user = userRepository.findByUuid(UUID.fromString(uuid))
                .orElseThrow(() -> new EntityNotFoundException("User not found!"));

        if (user.isDeleted()) {
            throw new UserDeletedException("User account is deleted!");
        }

        String newRefreshToken = jwtUtil.generateRefreshToken(user.getUuid());
        String newRefreshTokenJti = jwtUtil.getJtiFromToken(newRefreshToken);

        redisService.saveRefreshTokenJti(user.getUuid().toString(), newRefreshTokenJti, jwtUtil.getRefreshTokenExpiration(),
                java.util.concurrent.TimeUnit.MILLISECONDS);

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUuid(), user.getEmail(),
                user.getUserRoles().stream().map(Enum::name).collect(Collectors.toSet()));

        return new TokenResponseDTO(accessToken, newRefreshToken);
    }

    // Get user
    public UserResponseDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return userMapper.toUserResponseDTO(user);
    }

    // Update user profile
    @Transactional
    public UserResponseDTO updateUserProfile(String email, UserUpdateRequestDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        userMapper.updateUserFromUserUpdateRequestDTO(dto, user);

        userRepository.save(user);

        return userMapper.toUserResponseDTO(user);
    }

    // Update user password
    @Transactional
    public void updateUserPassword(String email, UserPasswordUpdateRequestDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if (!passwordEncoder.matches(dto.oldPassword(), user.getPassword())) {
            throw new IncorrectPasswordException("Incorrect old password!");
        }

        String encodedPassword = passwordEncoder.encode(dto.newPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);

        redisService.deleteRefreshTokenJti(user.getUuid().toString());
    }
}
