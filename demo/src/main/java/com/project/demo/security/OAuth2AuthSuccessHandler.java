package com.project.demo.security;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.project.demo.enumeration.AccountStatus;
import com.project.demo.enumeration.AuthProvider;
import com.project.demo.enumeration.Role;
import com.project.demo.model.User;
import com.project.demo.repository.UserRepository;
import com.project.demo.service.RedisService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2AuthSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final RedisService redisService;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

            DefaultOAuth2User oauth2User = (DefaultOAuth2User) authentication.getPrincipal();

            String email = oauth2User.getAttribute("email");

            Optional<User> userOptional = userRepository.findByEmail(email);
            User user;

            if (userOptional.isPresent()) {
                user = userOptional.get();
                user.setAuthProvider(AuthProvider.GOOGLE);
                user.setName(oauth2User.getAttribute("name"));

                updateLoginInfo(user, request);
            } else {
                user = createNewOAuth2User(oauth2User, request);
            }

            userRepository.save(user);

            String oauth2Code = UUID.randomUUID().toString();
            redisService.saveOAuth2AuthCode(oauth2Code, user.getUuid().toString(), 5, TimeUnit.MINUTES);

            String redirectUrl = frontendUrl + "?oauth2Code=" + oauth2Code;
            response.sendRedirect(redirectUrl);
    }

    private User createNewOAuth2User(DefaultOAuth2User oauth2User, HttpServletRequest request) {
        User user = new User();

        user.setUuid(UUID.randomUUID());
        user.setEmail(oauth2User.getAttribute("email"));
        user.setName(oauth2User.getAttribute("name"));
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setUserRoles(Set.of(Role.CUSTOMER));
        user.setAuthProvider(AuthProvider.GOOGLE);
        user.setCreatedAt(LocalDateTime.now());
        user.setCreatedBy("oauth2");

        return user;
    }

    private void updateLoginInfo(User user, HttpServletRequest request) {
        user.setLastLoginAt(LocalDateTime.now());
        user.setLastLoginIp(request.getRemoteAddr());
        user.setUpdatedAt(LocalDateTime.now());
        user.setUpdatedBy("oauth2");
    }
}
