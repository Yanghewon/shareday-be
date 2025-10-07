package com.shareday.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info(">>> [CustomOAuth2UserService] OAuth2UserRequest = {}", userRequest);

        // Spring 기본 로직으로 user info 요청
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // providerId (google, naver, kakao)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("✅ Provider(registrationId) = {}", registrationId);

        // access_token 확인
        String accessToken = userRequest.getAccessToken().getTokenValue();
        log.info("✅ Access Token = {}", accessToken);

        // 원본 attribute 데이터
        Map<String, Object> attributes = oAuth2User.getAttributes();
        log.info("✅ Raw Attributes = {}", attributes);

        try {
            switch (registrationId) {
                case "google" -> handleGoogle(attributes);
                case "naver" -> handleNaver(attributes);
                case "kakao" -> handleKakao(attributes);
                default -> log.warn("⚠️ Unknown provider: {}", registrationId);
            }
        } catch (Exception e) {
            log.error("❌ Error while parsing OAuth2 attributes for {}", registrationId, e);
            throw e;
        }

        // 그대로 반환 — SecurityConfig의 successHandler에서 후처리함
        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                oAuth2User.getAttributes(),
                "id" // 기본 key (provider마다 다름)
        );
    }

    private void handleGoogle(Map<String, Object> attributes) {
        log.info("🌐 [Google] name={}, email={}",
                attributes.get("name"),
                attributes.get("email"));
    }

    private void handleNaver(Map<String, Object> attributes) {
        Object responseObj = attributes.get("response");
        if (responseObj instanceof Map<?, ?> response) {
            log.info("💚 [Naver] name={}, email={}", response.get("name"), response.get("email"));
        } else {
            log.warn("⚠️ Naver response attribute missing!");
        }
    }

    private void handleKakao(Map<String, Object> attributes) {
        Object kakaoAccountObj = attributes.get("kakao_account");
        if (kakaoAccountObj instanceof Map<?, ?> kakaoAccount) {
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            String nickname = profile != null ? (String) profile.get("nickname") : null;
            String email = (String) kakaoAccount.get("email");
            log.info("🟡 [Kakao] nickname={}, email={}", nickname, email);
        } else {
            log.warn("⚠️ Kakao kakao_account attribute missing!");
        }
    }
}
