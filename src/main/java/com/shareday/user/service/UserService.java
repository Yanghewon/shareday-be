package com.shareday.user.service;

import com.shareday.user.dto.UserResponse;
import com.shareday.user.entity.User;
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
        String kakaoId = null;

        // ✅ 1️⃣ 일반 JWT 로그인
        if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        }

        // ✅ 2️⃣ OAuth2 로그인 (기본 DefaultOAuth2User 사용)
        else if (principal instanceof DefaultOAuth2User oAuth2User) {
            Map<String, Object> attrs = oAuth2User.getAttributes();

            if (attrs.containsKey("email")) {
                // Google
                email = attrs.get("email").toString();
            } else if (attrs.containsKey("response")) {
                // Naver
                Map<String, Object> res = (Map<String, Object>) attrs.get("response");
                email = res != null ? (String) res.get("email") : null;
            } else if (attrs.containsKey("kakao_account")) {
                // Kakao
                Map<String, Object> kakaoAccount = (Map<String, Object>) attrs.get("kakao_account");
                if (kakaoAccount != null) {
                    email = (String) kakaoAccount.get("email");
                    kakaoId = attrs.get("id").toString(); // ✅ Kakao 고유 ID 저장용
                }
            }
        }

        if (email == null || email.isBlank()) {
            System.out.println("⚠️ [WARN] 이메일 정보 없음. Kakao ID fallback 사용");

            if (kakaoId != null) {
                final String finalKakaoId = kakaoId; // ✅ 람다용 final 복사
                return userRepository.findByKakaoId(finalKakaoId)
                        .map(UserResponse::from)
                        .orElseThrow(() -> new RuntimeException("Kakao ID에 해당하는 사용자를 찾을 수 없습니다. id=" + finalKakaoId));
            }

            throw new RuntimeException("이메일 정보를 가져올 수 없습니다.");
        }

        System.out.println("🔥 [DEBUG] 인증된 사용자 이메일: " + email);

        final String finalEmail = email; // ✅ 람다용 final 복사

        return userRepository.findByEmail(finalEmail)
                .map(UserResponse::from)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. email=" + finalEmail));
    }
}
