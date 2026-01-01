package com.project.demo.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

  private final StringRedisTemplate redisTemplate;

  // ===== Blacklist of Access Token (jti) =====
  public void saveAccessTokenJtiToBlacklist(String jti, long duration, TimeUnit unit) {
    redisTemplate.opsForValue().set("blacklist:access_token:" + jti, "true", duration, unit);
  }

  public boolean isAccessTokenJtiInBlacklist(String jti) {
    return Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:access_token:" + jti));
  }

  // ===== Whitelist of Refresh Token (jti) ===== 
  public void saveRefreshTokenJti(String userId, String jti, long duration, TimeUnit unit) {
    redisTemplate.opsForValue().set("refresh_token_jti:" + userId, jti, duration, unit);
  }
  
  public String getRefreshTokenJti(String userId) {
    return redisTemplate.opsForValue().get("refresh_token_jti:" + userId);
  }

  public void deleteRefreshTokenJti(String userId) {
    redisTemplate.delete("refresh_token_jti:" + userId);
  }

  // ===== Whitelist of Oauth2 Auth Code ===== 
  public void saveOauth2AuthCode(String oauth2Code, String userId, long duration, TimeUnit unit) {
    redisTemplate.opsForValue().set("oauth2authcode:" + oauth2Code, userId, duration, unit);
  }

  public String getOauth2AuthCode(String oauth2Code) {
    return redisTemplate.opsForValue().get("oauth2authcode:" + oauth2Code);
  }

  public void deleteOauth2AuthCode(String oauth2Code) {
    redisTemplate.delete("oauth2authcode:" + oauth2Code);
  }
}

