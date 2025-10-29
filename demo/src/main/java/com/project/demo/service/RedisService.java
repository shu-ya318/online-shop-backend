package com.project.demo.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

  private final StringRedisTemplate redisTemplate;

  // ===== JWT =====

  public void blacklistToken(String token, long duration, TimeUnit unit) {
    redisTemplate.opsForValue().set("blacklist:" + token, "true", duration, unit);
  }

  public boolean isTokenBlacklisted(String token) {
    boolean isBlacklisted = redisTemplate.hasKey("blacklist:" + token);
    
    return isBlacklisted;
  }

  // Refresh Token
  public void saveRefreshToken(String userId, String refreshToken, long duration, TimeUnit unit) {
    redisTemplate.opsForValue().set("refresh:" + userId, refreshToken, duration, unit);
  }
  
  public String getRefreshToken(String userId) {
    String refreshToken = redisTemplate.opsForValue().get("refresh:" + userId);

    return refreshToken;
  }

  public void deleteRefreshToken(String userId) {
    redisTemplate.delete("refresh_token:" + userId);
  }

  // ===== OAuth2 Authorization Code ===== 
  public void saveOAuth2AuthCode(String oauth2Code, String userId, long duration, TimeUnit unit) {
    redisTemplate.opsForValue().set("oauth2authcode:" + oauth2Code, userId, duration, unit);
  }

  public String getOAuth2AuthCode(String oauth2Code) {
    String authCode = redisTemplate.opsForValue().get("oauth2authcode:" + oauth2Code);

    return authCode;
  }

  public void deleteOAuth2AuthCode(String oauth2Code) {
    redisTemplate.delete("oauth2authcode:" + oauth2Code);
  }

  // ===== TODO: Account Activation Code ===== 
  public void saveActivationCode(String email, String code) {
    redisTemplate.opsForValue().set("activation:" + email, code, 15, TimeUnit.MINUTES);
  }

  public String getActivationCode(String email) {
    String activationCode = redisTemplate.opsForValue().get("activation:" + email);

    return activationCode;
  }

  public void deleteActivationCode(String email) {
    redisTemplate.delete("activation:" + email);
  }

  // ===== TODO: Password Reset Code ===== 
  public void savePasswordResetCode(String email, String code) {
    redisTemplate.opsForValue().set("pwdreset:" + email, code, 15, TimeUnit.MINUTES);
  }

  public String getPasswordResetCode(String email) {
    String resetCode = redisTemplate.opsForValue().get("pwdreset:" + email);

    return resetCode;
  }

  public void deletePasswordResetCode(String email) {
    redisTemplate.delete("pwdreset:" + email);
  }

  // ===== TODO: 2FA Verification Code ===== 
  public void saveTwoFactorCode(String email, String code) {
    redisTemplate.opsForValue().set("2fa:" + email, code, 5, TimeUnit.MINUTES);
  }

  public String getTwoFactorCode(String email) {
    String twoFactorCode = redisTemplate.opsForValue().get("2fa:" + email);

    return twoFactorCode;
  }

  public void deleteTwoFactorCode(String email) {
    redisTemplate.delete("2fa:" + email);
  }

  // ===== TODO: Failed Login Attempts ===== 
  public void incrementFailedLoginAttempts(String email) {
    String key = "failedlogin:" + email;
    Long count = redisTemplate.opsForValue().increment(key);

    if (count != null && count == 1) {
      redisTemplate.expire(key, 1, TimeUnit.HOURS);
    }
  }

  public int getFailedLoginAttempts(String email) {
    String val = redisTemplate.opsForValue().get("failedlogin:" + email);
    int attempts = val == null ? 0 : Integer.parseInt(val);

    return attempts;
  }

  public void resetFailedLoginAttempts(String email) {
    redisTemplate.delete("failedlogin:" + email);
  }

  // ===== TODO: Account Lock Until ===== 
  public void setAccountLockUntil(String email, long timestamp) {
    redisTemplate.opsForValue().set("lockuntil:" + email, String.valueOf(timestamp), 1, TimeUnit.HOURS);
  }

  public Long getAccountLockUntil(String email) {
    String val = redisTemplate.opsForValue().get("lockuntil:" + email);

    if (val == null)
      return null;
    try {
      Long lockUntil = Long.parseLong(val);
      return lockUntil;
    } catch (NumberFormatException e) {
      return null;
    }
  }

  public void removeAccountLock(String email) {
    redisTemplate.delete("lockuntil:" + email);
  }
}
