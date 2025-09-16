package com.shareday.auth.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 기본적으로 사용자 정보 불러오기
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 여기서 userRequest.getClientRegistration().getRegistrationId() 로 provider 구분 가능
        // oAuth2User.getAttributes() 로 사용자 정보 확인 가능
        // 원하는 로직 (DB 저장/갱신, 권한 부여 등) 수행

        return oAuth2User;
    }
}