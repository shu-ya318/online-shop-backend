package com.project.demo.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import com.project.demo.service.RedisService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LogoutResultHandler implements LogoutSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RedisService redisService;
    private final CookieUtil cookieUtil;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        try {
            // Get and remove refreshToken from Cookie
            String refreshToken = null;

            if (request.getCookies() != null) {
                refreshToken = Arrays.stream(request.getCookies())
                        .filter(c -> "refreshToken".equals(c.getName()))
                        .map(Cookie::getValue)
                        .findFirst()
                        .orElse(null);
            }

            // Delete refreshToken from Redis and cookies
            if (refreshToken != null) {
                String uuid = jwtUtil.getUuidFromRefreshToken(refreshToken);
                redisService.deleteRefreshTokenJti(uuid);
            }

            cookieUtil.clearRefreshTokenCookie(response);

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            Map<String, String> body = Map.of("message", "Logout successfully!");
            new ObjectMapper().writeValue(response.getWriter(), body);

        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            Map<String, String> body = Map.of("message", "Logout failed due to a server error!");
            new ObjectMapper().writeValue(response.getWriter(), body);
        }
    }
}
