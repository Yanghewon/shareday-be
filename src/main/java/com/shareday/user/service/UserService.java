package com.shareday.user.service;

import com.shareday.user.dto.UserResponse;
import com.shareday.user.entity.User;
import com.shareday.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @PersistenceContext
    private EntityManager em;

    /**
     * 현재 로그인한 사용자 정보를 반환한다.
     * - JWT 로그인: userId 기반 조회
     * - OAuth2 로그인: email 기반 조회
     */
    public UserResponse getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new RuntimeException("인증되지 않은 사용자입니다.");
        }

        Object principal = auth.getPrincipal();
        User user;

        // ✅ JWT 인증 (UserDetails - username == userId)
        if (principal instanceof UserDetails userDetails) {
            try {
                Long userId = Long.parseLong(userDetails.getUsername());
                user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. id=" + userId));
                log.debug("✅ JWT 사용자 조회 성공: id={}", userId);
            } catch (NumberFormatException e) {
                throw new RuntimeException("잘못된 JWT 사용자 ID 형식입니다.");
            }
        }

        // ✅ OAuth2 로그인 (DefaultOAuth2User - email 기반)
        else if (principal instanceof DefaultOAuth2User oAuth2User) {
            String email = extractEmailFromOAuthAttributes(oAuth2User.getAttributes());

            if (email == null || email.isBlank()) {
                throw new RuntimeException("이메일 정보를 가져올 수 없습니다.");
            }

            user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. email=" + email));
            log.debug("✅ OAuth2 사용자 조회 성공: email={}", email);
        }

        // ❌ 알 수 없는 principal 타입
        else {
            throw new RuntimeException("지원하지 않는 인증 방식입니다: " + principal.getClass().getSimpleName());
        }

        // ✅ Lazy 로딩 방지용 (커플 정보 포함)
        if (user.getCouple() != null) {
            em.refresh(user.getCouple());
        }

        return UserResponse.from(user);
    }

    /**
     * OAuth2 로그인 시 provider별로 이메일 추출
     */
    private String extractEmailFromOAuthAttributes(Map<String, Object> attrs) {
        if (attrs == null) return null;

        if (attrs.containsKey("email")) {
            return (String) attrs.get("email");
        } else if (attrs.containsKey("response")) { // Naver
            Map<String, Object> res = safeCastToMap(attrs.get("response"));
            return res != null ? (String) res.get("email") : null;
        } else if (attrs.containsKey("kakao_account")) { // Kakao
            Map<String, Object> kakaoAccount = safeCastToMap(attrs.get("kakao_account"));
            return kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> safeCastToMap(Object obj) {
        try {
            return obj != null ? (Map<String, Object>) obj : null;
        } catch (ClassCastException e) {
            return null;
        }
    }
    @Transactional(readOnly = true)
    public UserResponse getMe(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. id=" + userId));

        // Lazy 로딩 방지 (커플 정보 포함)
        if (user.getCouple() != null) {
            em.refresh(user.getCouple());
        }

        return UserResponse.from(user);
    }

}
