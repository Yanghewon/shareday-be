package com.shareday.auth.service;

import com.shareday.auth.enums.ProviderType;
import com.shareday.user.entity.User;
import com.shareday.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final AuthService authService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        var delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // kakao | google | naver
        log.info(">>> [CustomOAuth2UserService] OAuth2UserRequest = {}", userRequest);
        log.info("✅ Provider(registrationId) = {}", registrationId);
        log.info("✅ Access Token = {}", userRequest.getAccessToken().getTokenValue());
        log.info("✅ Raw Attributes = {}", oAuth2User.getAttributes());

        // 원본은 UnmodifiableMap 일 수 있으므로 반드시 복사해서 사용
        Map<String, Object> src = oAuth2User.getAttributes();
        Map<String, Object> attrs = new HashMap<>(src);

        String providerId = null;
        String email = null;
        String nickname = null;
        ProviderType provider;

        switch (registrationId) {
            case "google" -> {
                provider = ProviderType.GOOGLE;
                providerId = (String) src.get("sub");                    // Google은 sub
                email = (String) src.get("email");
                nickname = (String) src.getOrDefault("name", email);

                // 루트에 항상 "id" 존재하도록 평탄화
                attrs.put("id", providerId);
            }
            case "kakao" -> {
                provider = ProviderType.KAKAO;
                providerId = String.valueOf(src.get("id"));              // Kakao는 id
                Map<String, Object> kakaoAccount = getMap(src, "kakao_account");
                if (kakaoAccount != null) {
                    email = (String) kakaoAccount.get("email");
                    Map<String, Object> profile = getMap(kakaoAccount, "profile");
                    if (profile != null) {
                        nickname = (String) profile.get("nickname");
                    }
                }
                if (nickname == null) {
                    Map<String, Object> properties = getMap(src, "properties");
                    if (properties != null) nickname = (String) properties.get("nickname");
                }

                attrs.put("id", providerId);
            }
            case "naver" -> {
                provider = ProviderType.NAVER;
                Map<String, Object> response = getMap(src, "response");
                if (response != null) {
                    providerId = (String) response.get("id");           // Naver는 response.id
                    email = (String) response.get("email");
                    nickname = (String) response.getOrDefault("name", email);
                }

                attrs.put("id", providerId);
                // 필요 시 다른 response.* 값도 평탄화해서 루트로 올릴 수 있음
            }
            default -> throw new IllegalArgumentException("지원하지 않는 OAuth provider : " + registrationId);
        }

        log.info("🌐 [{}] nickname={}, email={}, id={}", registrationId, nickname, email, providerId);

        // 사용자 저장/업데이트 (User에서 kakaoId 제거했으므로 providerId는 저장 안 해도 됨)
        User user = authService.saveOrUpdate(
                email,
                nickname,
                ProviderType.valueOf(registrationId.toUpperCase()),
                providerId // kakaoId 삭제했어도 로깅/추후 용도로 전달은 가능
        );

        // Spring Security 세션에 넣을 OAuth2User
        Collection<? extends GrantedAuthority> authorities = oAuth2User.getAuthorities();
        // usernameAttributeName은 우리가 보장한 "id"로 사용
        return new DefaultOAuth2User(authorities, attrs, "id");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getMap(Map<String, Object> map, String key) {
        Object obj = map.get(key);
        return (obj instanceof Map) ? (Map<String, Object>) obj : null;
    }
}
