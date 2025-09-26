package com.shareday.common.config;

import com.shareday.auth.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login/**").permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/api/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler((request, response, authentication) -> {
                            var oauthUser = (org.springframework.security.oauth2.core.user.OAuth2User) authentication.getPrincipal();
                            var auth = (org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken) authentication;
                            String registrationId = auth.getAuthorizedClientRegistrationId(); // google, naver, kakao

                            String name = null;

                            switch (registrationId) {
                                case "google" -> {
                                    name = oauthUser.getAttribute("name");
                                }
                                case "naver" -> {
                                    var responseMap = (Map<String, Object>) oauthUser.getAttribute("response");
                                    if (responseMap != null) {
                                        name = (String) responseMap.get("name");
                                        if (name == null) {
                                            name = (String) responseMap.get("nickname");
                                        }
                                    }
                                }
                                case "kakao" -> {
                                    var accountMap = (Map<String, Object>) oauthUser.getAttribute("kakao_account");
                                    if (accountMap != null) {
                                        var profileMap = (Map<String, Object>) accountMap.get("profile");
                                        if (profileMap != null) {
                                            name = (String) profileMap.get("nickname");
                                        }
                                    }
                                }
                            }

                            if (name == null) {
                                name = "사용자"; // fallback 방어 코드
                            }

                            String redirectUrl = "http://localhost:5173/?userName=" +
                                    URLEncoder.encode(name, StandardCharsets.UTF_8);

                            response.sendRedirect(redirectUrl);
                        })
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(allowedOrigins));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
