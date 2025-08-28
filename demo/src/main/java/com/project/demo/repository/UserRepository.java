package com.project.demo.repository;

import org.springframework.stereotype.Repository;

import com.project.demo.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	// ===== 前台 =====
	boolean existsByEmail(String email);

	Optional<User> findByEmail(String email);

	Optional<User> findByUuid(String uuid);

	Optional<User> findByRefreshToken(String refreshToken);

    // ===== 後台 =====

}
