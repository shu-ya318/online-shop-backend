package com.project.demo.service;

import com.project.demo.dto.user.UserRegisterRequestDTO;
import com.project.demo.mapper.UserMapper;
import com.project.demo.model.User;
import com.project.demo.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public void register(UserRegisterRequestDTO dto) {
        // 檢查 email 是否已註冊且未被刪除
        Optional<User> optionalUser = userRepository.findByEmail(dto.email());
        
        User user;
        if (optionalUser.isPresent()) {
            if (!optionalUser.get().isDeleted()) {
                throw new IllegalArgumentException("Email already registered");
            }

            // 軟刪除帳號再註冊：覆寫舊資料
            user = optionalUser.get();
            userMapper.updateUserFromUserRegisterRequestDTO(dto, user);
        } else {
            // 新使用者
            user = userMapper.toUser(dto);
        }
        
        user.setUuid(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(dto.password()));
        
        try {
            logger.info("Attempting to save user with email: {}", user.getEmail());
            userRepository.save(user);
            logger.info("Successfully saved user with email: {}", user.getEmail());
        } catch (DataAccessException e) {
            logger.error("Database error while saving user with email: {}", user.getEmail(), e);
            throw new RuntimeException("Database error during registration.", e);
        }

        // 產生驗證碼
//        String activationCode = generateActivationCode();

        // 將驗證碼存入 Redis
//        redisCacheService.saveActivationCode(user.getEmail(), activationCode);

        // 寄驗證信
        // mailService.sendActivationEmail(user.getEmail(), activationCode);
    }
    
//    public void activate(String email, String code) {
//        String redisCode = redisCacheService.getActivationCode(email);
//
//        
//        if (redisCode == null) {
//            throw new RuntimeException("Activation code expired or not found.");
//        }
//
//        if (!redisCode.equals(code)) {
//            throw new RuntimeException("Invalid activation code.");
//        }
//
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        if (user.isDeleted()) {
//            throw new RuntimeException("User account is deleted.");
//        }
//
//        if (user.getAccountStatus() == AccountStatus.ACTIVE) {
//            throw new RuntimeException("Account is already activated.");
//        }
//
//        user.setAccountStatus(AccountStatus.ACTIVE);
//        userRepository.save(user);
//
//        // 啟用成功，刪除 Redis 驗證碼
//        redisCacheService.deleteActivationCode(email);
//    }
//    
//    // 產生六位數字驗證碼
//    private String generateActivationCode() {
//        int code = (int)(Math.random() * 900000) + 100000;
//        return String.valueOf(code);
//    }
}
