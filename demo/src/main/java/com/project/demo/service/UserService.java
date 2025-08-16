package com.project.demo.service;

import com.project.demo.dto.user.UserRegisterRequestDTO;
import com.project.demo.mapper.UserMapper;
import com.project.demo.model.User;
import com.project.demo.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

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

            // 軟刪除帳號再註冊：覆寫舊資料
            user = optionalUser.get();
            userMapper.updateUserFromUserRegisterRequestDTO(dto, user);
        } else {
            // 新使用者
            user = userMapper.toUser(dto);
        }

        user.setUuid(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(dto.password()));
        userRepository.save(user);
    }

    @Transactional
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
