package com.project.demo.security;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.project.demo.enumeration.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {

	private final SecretKey key;

	private final long expiration;

	public JwtUtil(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") long expiration) {
		try {
			if (secret == null || secret.trim().isEmpty()) {
				throw new IllegalArgumentException("JWT secret is empty");
			}
			this.key = Keys.hmacShaKeyFor(secret.getBytes());
			this.expiration = expiration;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public String generateToken(String email, Set<Role> roles) {
		try {
			Claims claims = Jwts.claims().setSubject(email);
			var rolesList = roles.stream().map(Enum::name).collect(Collectors.toList());
			claims.put("roles", rolesList);

			Date now = new Date();
			Date expiryDate = new Date(now.getTime() + expiration);

			String token = Jwts.builder()
					.setClaims(claims)
					.setIssuedAt(now)
					.setExpiration(expiryDate)
					.signWith(key, SignatureAlgorithm.HS256)
					.compact();
			
			return token;
		} catch (Exception e) {
			throw e;
		}
	}

	public String getEmailFromToken(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}
}
