package com.shareday.user.service;

import com.shareday.user.dto.UserResponse;
import com.shareday.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new RuntimeException("인증되지 않은 사용자입니다.");
        }

        Object principal = auth.getPrincipal();
        String email = null;

        // 1) 일반 JWT 로그인
        if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        }
        // 2) OAuth2 로그인
        else if (principal instanceof DefaultOAuth2User oAuth2User) {
            Map<String, Object> attrs = oAuth2User.getAttributes();

            if (attrs.containsKey("email")) {
                // Google 또는 CustomOAuth2UserService에서 평탄화한 email
                email = attrs.get("email") != null ? attrs.get("email").toString() : null;
            } else if (attrs.containsKey("response")) {
                // Naver
                Map<String, Object> res = safeCastToMap(attrs.get("response"));
                email = res != null ? (String) res.get("email") : null;
            } else if (attrs.containsKey("kakao_account")) {
                // Kakao
                Map<String, Object> kakaoAccount = safeCastToMap(attrs.get("kakao_account"));
                email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
            }
        }

        if (email == null || email.isBlank()) {
            throw new RuntimeException("이메일 정보를 가져올 수 없습니다.");
        }

        // ✅ 람다에서 사용할 final 복사본
        final String finalEmail = email;

        return userRepository.findByEmail(finalEmail)
                .map(UserResponse::from)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. email=" + finalEmail));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> safeCastToMap(Object obj) {
        try {
            return obj != null ? (Map<String, Object>) obj : null;
        } catch (ClassCastException e) {
            return null;
        }
    }
}
