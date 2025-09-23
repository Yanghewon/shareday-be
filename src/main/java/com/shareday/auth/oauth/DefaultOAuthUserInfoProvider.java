package com.shareday.auth.oauth;

import com.shareday.auth.dto.OAuthUserInfo;
import com.shareday.auth.enums.ProviderType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

import java.util.Map;

@Component
public class DefaultOAuthUserInfoProvider implements OAuthUserInfoProvider {

    private final RestTemplate restTemplate;

    public DefaultOAuthUserInfoProvider(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public OAuthUserInfo getUserInfo(ProviderType provider, String accessToken) {
        return switch (provider) {
            case google -> fetchGoogleUser(accessToken);
            case naver -> fetchNaverUser(accessToken);
            case kakao -> fetchKakaoUser(accessToken);
        };
    }

    private OAuthUserInfo fetchGoogleUser(String token) {
        String url = "https://www.googleapis.com/oauth2/v2/userinfo";
        HttpEntity<Void> entity = createAuthEntity(token);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        Map<String, Object> body = response.getBody();
        return new OAuthUserInfo(
                ProviderType.google,
                (String) body.get("id"),
                (String) body.get("email"),
                (String) body.get("name")
        );
    }

    private OAuthUserInfo fetchNaverUser(String token) {
        String url = "https://openapi.naver.com/v1/nid/me";
        HttpEntity<Void> entity = createAuthEntity(token);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        Map<String, Object> resp = (Map<String, Object>) response.getBody().get("response");
        return new OAuthUserInfo(
                ProviderType.naver,
                (String) resp.get("id"),
                (String) resp.get("email"),
                (String) resp.get("name")
        );
    }

    private OAuthUserInfo fetchKakaoUser(String token) {
        String url = "https://kapi.kakao.com/v2/user/me";
        HttpEntity<Void> entity = createAuthEntity(token);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        Map<String, Object> body = response.getBody();
        Map<String, Object> kakaoAccount = (Map<String, Object>) body.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        return new OAuthUserInfo(
                ProviderType.kakao,
                String.valueOf(body.get("id")),
                (String) kakaoAccount.get("email"),
                (String) profile.get("nickname")
        );
    }

    private HttpEntity<Void> createAuthEntity(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return new HttpEntity<>(headers);
    }
}
