package com.project.demo.security;

import java.util.UUID;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.StringRedisTemplate;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.access-token-expiration}")
	private long accessTokenExpiration;

	@Value("${jwt.refresh-token-expiration}")
	private long refreshTokenExpiration;

	private final SecretKey key;

	private final StringRedisTemplate redisTemplate;

	public JwtUtil(@Value("${jwt.secret}") String secret,
			@Value("${jwt.access-token-expiration}") long accessTokenExpiration,
			@Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration,
			StringRedisTemplate redisTemplate) {
		try {
			if (secret == null || secret.trim().isEmpty()) {
				throw new IllegalArgumentException("JWT secret is empty");
			}
			this.key = Keys.hmacShaKeyFor(secret.getBytes());
			this.accessTokenExpiration = accessTokenExpiration;
			this.refreshTokenExpiration = refreshTokenExpiration;
			this.redisTemplate = redisTemplate;
		} catch (Exception e) {
			throw e;
		}
	}

	public String generateAccessToken(Long id, UUID uuid, String email, Set<String> roles) {
		try {
			String accessToken = Jwts.builder()
					.setSubject(uuid.toString())
					.claim("id", id)
					.claim("email", email)
					.claim("roles", roles)
					.setIssuedAt(new Date())
					.setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
					.signWith(key, SignatureAlgorithm.HS256)
					.compact();

			return accessToken;
		} catch (Exception e) {
			throw e;
		}
	}

	public String generateRefreshToken(UUID uuid) {
		try {
			String refreshToken = Jwts.builder()
					.setSubject(uuid.toString())
					.setIssuedAt(new Date())
					.setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
					.signWith(key, SignatureAlgorithm.HS256)
					.compact();

			redisTemplate.opsForValue().set("refresh_token:" + uuid, refreshToken, refreshTokenExpiration,
					TimeUnit.MILLISECONDS);

			return refreshToken;
		} catch (Exception e) {
			throw e;
		}
	}

	public boolean validateAccessToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);

			return true;
		} catch (JwtException | IllegalArgumentException e) {

			return false;
		}
	}

	public boolean validateRefreshToken(UUID uuid, String refreshToken) {
		try {
			String stored = redisTemplate.opsForValue().get("refresh_token:" + uuid.toString());

			if (stored == null || !stored.equals(refreshToken)) {
				return false;
			}

			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(refreshToken);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		} catch (Exception e) {
			throw e;
		}
	}

	public String refreshAccessToken(Long id, UUID uuid, String email, Set<String> roles, String refreshToken) {
		if (!validateRefreshToken(uuid, refreshToken))
			throw new RuntimeException("Invalid refresh token");

		return generateAccessToken(id, uuid, email, roles);
	}

	public void revokeRefreshToken(UUID uuid) {
		redisTemplate.delete("refresh_token:" + uuid.toString());
	}

	public long getRefreshTokenExpiration() {
		return refreshTokenExpiration;
	}

	public String getEmailFromToken(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("email",
				String.class);
	}

	public String getUuidFromRefreshToken(String refreshToken) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(refreshToken).getBody().getSubject();
	}
}
