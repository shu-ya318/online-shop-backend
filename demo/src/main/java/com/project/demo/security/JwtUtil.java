package com.project.demo.security;

import java.util.UUID;
import java.util.Date;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.ExpiredJwtException;
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
	private final JwtParser jwtParser;

	public JwtUtil(@Value("${jwt.secret}") String secret,
			@Value("${jwt.access-token-expiration}") long accessTokenExpiration,
			@Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration) {
		if (secret == null || secret.trim().isEmpty()) {
			throw new IllegalArgumentException("JWT secret is empty");
		}

		this.key = Keys.hmacShaKeyFor(secret.getBytes());
		this.accessTokenExpiration = accessTokenExpiration;
		this.refreshTokenExpiration = refreshTokenExpiration;
		this.jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
	}

	public String generateAccessToken(Long id, UUID uuid, String email, Set<String> roles) {
		try {
			return Jwts.builder()
					.setId(UUID.randomUUID().toString())
					.setSubject(uuid.toString())
					.claim("id", id)
					.claim("email", email)
					.claim("roles", roles)
					.setIssuedAt(new Date())
					.setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
					.signWith(key, SignatureAlgorithm.HS256)
					.compact();
		} catch (Exception e) {
			throw e;
		}
	}

	public String generateRefreshToken(UUID uuid) {
		try {
			return Jwts.builder()
					.setId(UUID.randomUUID().toString())
					.setSubject(uuid.toString())
					.setIssuedAt(new Date())
					.setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
					.signWith(key, SignatureAlgorithm.HS256)
					.compact();
		} catch (Exception e) {
			throw e;
		}
	}

	public boolean validateAccessToken(String token) {
		try {
			jwtParser.parseClaimsJws(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	public boolean validateRefreshToken(String refreshToken) {
		try {
			jwtParser.parseClaimsJws(refreshToken);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	public long getRefreshTokenExpiration() {
		return refreshTokenExpiration;
	}

	public String getEmailFromToken(String token) {
		return jwtParser.parseClaimsJws(token).getBody().get("email", String.class);
	}

	public String getUuidFromRefreshToken(String refreshToken) throws JwtException {
		return jwtParser.parseClaimsJws(refreshToken).getBody().getSubject();
	}
	
	public String getJtiFromToken(String token) {
		return jwtParser.parseClaimsJws(token).getBody().getId();
	}
	
	public Date getExpirationDateFromToken(String token) {
		return jwtParser.parseClaimsJws(token).getBody().getExpiration();
	}
}
