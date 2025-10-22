package com.shareday.common.config;

import com.shareday.auth.enums.ProviderType;
import com.shareday.auth.filter.JwtAuthenticationFilter;
import com.shareday.auth.oauth.JwtProvider;
import com.shareday.auth.service.AuthService;
import com.shareday.auth.service.CustomOAuth2UserService;
import com.shareday.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtProvider jwtProvider;
    private final AuthService authService;

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .formLogin(AbstractHttpConfigurer::disable)
                // ⬇️ 인증 실패를 401 JSON으로 고정
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, ex1) -> {
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write("{\"message\":\"Unauthorized\"}");
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/login/**", "/auth/**",
                                "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler((request, response, authentication) -> {
                            log.info(">>> [OAuth2 SuccessHandler] authentication = {}", authentication);

                            if (authentication instanceof org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken authTokenDebug) {
                                log.info(">>> [OAuth2 SuccessHandler] registrationId = {}", authTokenDebug.getAuthorizedClientRegistrationId());
                            }

                            var oauthUser = (org.springframework.security.oauth2.core.user.OAuth2User) authentication.getPrincipal();
                            var authToken = (org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken) authentication;
                            String registrationId = authToken.getAuthorizedClientRegistrationId(); // google, naver, kakao

                            log.info("✅ Provider(registrationId) = {}", registrationId);
                            log.info("✅ OAuth2User Attributes = {}", oauthUser.getAttributes());

                            String email = null;
                            String nickname = null;
                            String providerId = null;

                            var providerType = com.shareday.auth.enums.ProviderType.valueOf(registrationId.toUpperCase());
                            log.info("✅ ProviderType Enum = {}", providerType);

                            switch (registrationId) {
                                case "google" -> {
                                    email = oauthUser.getAttribute("email");
                                    nickname = oauthUser.getAttribute("name");
                                    providerId = oauthUser.getName();
                                }
                                case "naver" -> {
                                    var responseMap = (Map<String, Object>) oauthUser.getAttribute("response");
                                    if (responseMap != null) {
                                        email = (String) responseMap.get("email");
                                        nickname = (String) responseMap.get("name");
                                        if (nickname == null) nickname = (String) responseMap.get("nickname");
                                        providerId = (String) responseMap.get("id");
                                    }
                                }
                                case "kakao" -> {
                                    providerId = oauthUser.getAttribute("id").toString();
                                    var accountMap = (Map<String, Object>) oauthUser.getAttribute("kakao_account");
                                    if (accountMap != null) {
                                        email = (String) accountMap.get("email");
                                        var profileMap = (Map<String, Object>) accountMap.get("profile");
                                        if (profileMap != null) {
                                            nickname = (String) profileMap.get("nickname");
                                        }
                                    }
                                }
                            }

                            if (nickname == null) nickname = "사용자";

                            // ✅ 카카오 이메일 없으면 대체 이메일 생성
                            if (providerType == ProviderType.KAKAO && (email == null || email.isBlank())) {
                                email = "kakao_" + providerId + "@kakao.com";
                                log.warn("⚠️ 카카오 이메일 없음 -> 대체 email 생성: {}", email);
                            }

                            // ✅ AuthService를 통해 Auth/User 저장 & 조회
                            User userEntity = authService.saveOrUpdate(email, nickname, providerType, providerId);

                            // ✅ JWT 발급
                            String jwtToken = jwtProvider.generateToken(userEntity.getId(), userEntity.getEmail());
                            log.info("✅ JWT 발급 완료 -> {}", jwtToken);

                            // ✅ 프론트 리다이렉트 (token + userName 같이 전달)
                            String redirectUrl = "http://localhost:5173/oauth/success?token=" +
                                    URLEncoder.encode(jwtToken, StandardCharsets.UTF_8) +
                                    "&userName=" +
                                    URLEncoder.encode(userEntity.getNickname(), StandardCharsets.UTF_8);

                            log.info("✅ Redirect to frontend -> {}", redirectUrl);
                            response.sendRedirect(redirectUrl);
                        })
                        .failureHandler((request, response, exception) -> {
                            log.error("❌ [OAuth2 FailureHandler] 로그인 실패", exception);
                            String redirectUrl = "http://localhost:5173/login?error=" +
                                    URLEncoder.encode(exception.getMessage(), StandardCharsets.UTF_8);
                            response.sendRedirect(redirectUrl);
                        })
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                )
                // ✅ JWT 인증 필터 추가
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtProvider),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(allowedOrigins));  // CORS Origins 설정
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")); // 허용할 HTTP 메소드들
        configuration.setAllowedHeaders(List.of("*", "Authorization"));  // Authorization 헤더 포함
        configuration.setAllowCredentials(true);  // 쿠키/세션 정보 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
