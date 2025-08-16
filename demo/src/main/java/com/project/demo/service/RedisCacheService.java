package com.project.demo.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisCacheService {

    private final StringRedisTemplate redisTemplate;

    public RedisCacheService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveActivationCode(String email, String code) {
        redisTemplate.opsForValue().set("activation:" + email, code, 15, TimeUnit.MINUTES);
    }
    public String getActivationCode(String email) {
        return redisTemplate.opsForValue().get("activation:" + email);
    }
    public void deleteActivationCode(String email) {
        redisTemplate.delete("activation:" + email);
    }

    public void savePasswordResetCode(String email, String code) {
        redisTemplate.opsForValue().set("pwdreset:" + email, code, 15, TimeUnit.MINUTES);
    }
    public String getPasswordResetCode(String email) {
        return redisTemplate.opsForValue().get("pwdreset:" + email);
    }
    public void deletePasswordResetCode(String email) {
        redisTemplate.delete("pwdreset:" + email);
    }

    public void saveTwoFactorCode(String email, String code) {
        redisTemplate.opsForValue().set("2fa:" + email, code, 5, TimeUnit.MINUTES);
    }
    public String getTwoFactorCode(String email) {
        return redisTemplate.opsForValue().get("2fa:" + email);
    }
    public void deleteTwoFactorCode(String email) {
        redisTemplate.delete("2fa:" + email);
    }

    public void incrementFailedLoginAttempts(String email) {
        String key = "failedlogin:" + email;
        Long count = redisTemplate.opsForValue().increment(key);

        if (count != null && count == 1) {
            redisTemplate.expire(key, 1, TimeUnit.HOURS);
        }
    }
    public int getFailedLoginAttempts(String email) {
        String val = redisTemplate.opsForValue().get("failedlogin:" + email);

        return val == null ? 0 : Integer.parseInt(val);
    }
    public void resetFailedLoginAttempts(String email) {
        redisTemplate.delete("failedlogin:" + email);
    }

    public void setAccountLockUntil(String email, long timestamp) {
        redisTemplate.opsForValue().set("lockuntil:" + email, String.valueOf(timestamp), 1, TimeUnit.HOURS);
    }
    public Long getAccountLockUntil(String email) {
        String val = redisTemplate.opsForValue().get("lockuntil:" + email);

        if (val == null) return null;

        try {
            return Long.parseLong(val);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    public void removeAccountLock(String email) {
        redisTemplate.delete("lockuntil:" + email);
    }
}


