package com.project.demo.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import com.project.demo.security.OAuth2AuthSuccessHandler;
import com.project.demo.security.LogoutResultHandler;
import com.project.demo.security.JwtFilter;

import lombok.RequiredArgsConstructor;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

import static com.project.demo.data.PathConstantData.API_PUBLIC_ALL;
import static com.project.demo.data.PathConstantData.API_VUE;
import static com.project.demo.data.PathConstantData.API_DNS;
import static com.project.demo.data.PathConstantData.API_LOGOUT;
import static com.project.demo.data.PathConstantData.API_REFRESH_TOKEN;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtFilter jwtFilter;
        private final OAuth2AuthSuccessHandler oAuth2AuthSuccessHandler;
        private final LogoutResultHandler logoutResultHandler;

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
                        throws Exception {
                return authenticationConfiguration.getAuthenticationManager();
        }

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
                                                        .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(API_PUBLIC_ALL, API_REFRESH_TOKEN, API_LOGOUT).permitAll()
                                                .anyRequest().authenticated());
                return http.build();
        }

        @Bean
        CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration config = new CorsConfiguration();

                config.setAllowedOrigins(List.of(
                                API_VUE,
                                API_DNS));
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                config.setAllowedHeaders(List.of("*"));
                config.setAllowCredentials(true);
                config.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", config);

                return source;
        }

        @Bean
        public AuthenticationEntryPoint customAuthenticationEntryPoint() {
                return (request, response, authException) -> {
                        response.setContentType("application/json");
                        response.setStatus(HttpStatus.UNAUTHORIZED.value());

                        ObjectMapper mapper = new ObjectMapper();
                        String jsonResponse = mapper.writeValueAsString(
                                        java.util.Map.of("message", "Unauthorized: Token is invalid or expired"));

                        response.getWriter().write(jsonResponse);
                };
        }
}
