package com.project.demo.config;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.http.HttpStatus;

import com.project.demo.security.OAuth2AuthSuccessHandler;
import com.project.demo.security.LogoutResultHandler;
import com.project.demo.security.JwtFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.core.AuthenticationException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.boot.web.servlet.FilterRegistrationBean;

import static com.project.demo.data.PathConstantData.API_PUBLIC_ALL;
import static com.project.demo.data.PathConstantData.API_DNS;
import static com.project.demo.data.PathConstantData.API_LOGOUT;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

        private final JwtFilter jwtFilter;
        private final OAuth2AuthSuccessHandler oAuth2AuthSuccessHandler;
        private final LogoutResultHandler logoutResultHandler;

        @Value("${frontend.url}")
        private String frontendUrl;

        @Value("${app.cors.allowed-origins}")
        private List<String> allowedOrigins;


        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                        .cors(cors -> {
                        })
                        .csrf(csrf -> csrf.disable())
                        .sessionManagement(session -> session
                                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .oauth2Login(oauth2 -> oauth2
                                        .successHandler(oAuth2AuthSuccessHandler))
                        .logout(logout -> logout.logoutUrl(API_LOGOUT)
                                        .logoutSuccessHandler(logoutResultHandler)
                                        .clearAuthentication(true))
                        .exceptionHandling(exceptions -> exceptions
                                        .authenticationEntryPoint(customAuthenticationEntryPoint()))
                        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                        .authorizeHttpRequests(authorize -> authorize
                                        .requestMatchers(API_PUBLIC_ALL).permitAll()
                                        .anyRequest().authenticated());

                return http.build();
        }

        @Bean
        public FilterRegistrationBean<JwtFilter> jwtFilterRegistration(JwtFilter jwtFilter) {
                FilterRegistrationBean<JwtFilter> registration = new FilterRegistrationBean<>(jwtFilter);
        
                registration.setEnabled(false);
                
                return registration;
        }

        @Bean
        CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration config = new CorsConfiguration();

                List<String> allAllowedOrigins = new ArrayList<>(allowedOrigins);
                allAllowedOrigins.add(API_DNS);
                config.setAllowedOriginPatterns(allAllowedOrigins);
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                config.setAllowedHeaders(List.of("*"));
                config.setAllowCredentials(true);
                config.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", config);

                return source;
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
                        throws Exception {
                return authenticationConfiguration.getAuthenticationManager();
        }

        @Bean
        public AuthenticationEntryPoint customAuthenticationEntryPoint() {
                return (request, response, authException) -> {
                        log.warn("Authentication failed: {}", authException.getMessage());
                        
                        response.setContentType("application/json");
                        response.setStatus(HttpStatus.UNAUTHORIZED.value());

                        ObjectMapper mapper = new ObjectMapper();
                        String jsonResponse = mapper.writeValueAsString(
                                        java.util.Map.of("message", "Unauthorized: Token is invalid or expired"));

                        response.getWriter().write(jsonResponse);
                };
        }
}
