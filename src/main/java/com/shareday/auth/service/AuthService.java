package com.shareday.auth.service;

import com.shareday.auth.dto.OAuthUserInfo;
import com.shareday.auth.dto.SocialSignUpRequest;
import com.shareday.auth.dto.SocialSignUpResponse;
import com.shareday.auth.dto.SocialLoginRequest;
import com.shareday.auth.dto.SocialLoginResponse;
import com.shareday.auth.entity.Auth;
import com.shareday.auth.enums.ProviderType;
import com.shareday.auth.oauth.JwtProvider;
import com.shareday.auth.oauth.OAuthUserInfoProvider;
import com.shareday.auth.repository.AuthRepository;
import com.shareday.user.entity.User;
import com.shareday.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final OAuthUserInfoProvider oAuthUserInfoProvider;
    private final AuthRepository authRepository;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    /**
     * ✅ 소셜 회원가입
     */
    @Transactional
    public SocialSignUpResponse socialSignUp(SocialSignUpRequest request) {
        OAuthUserInfo userInfo =
                oAuthUserInfoProvider.getUserInfo(request.provider(), request.accessToken());

        if (userInfo == null || userInfo.email() == null || userInfo.email().isBlank()) {
            throw new IllegalArgumentException("유효한 이메일을 제공하지 않는 소셜 계정입니다.");
        }

        var existing = authRepository.findByEmail(userInfo.email());

        Auth auth = existing.orElseGet(() -> {
            ProviderType provider = (userInfo.provider() instanceof ProviderType p)
                    ? p
                    : ProviderType.valueOf(userInfo.provider().toString().toUpperCase());
            return authRepository.save(new Auth(userInfo.email(), userInfo.nickname(), provider));
        });

        boolean isNewUser = existing.isEmpty();

        if (isNewUser) {
            User user = User.builder()
                    .auth(auth)
                    .email(auth.getEmail())
                    .nickname(auth.getNickname())
                    .provider(auth.getProvider())
                    .build();
            userRepository.save(user);
        }

        // ✅ JWT는 Auth 기준으로 발급
        String token = jwtProvider.generateToken(auth.getId(), auth.getEmail());

        return new SocialSignUpResponse(
                auth.getId(),
                auth.getEmail(),
                auth.getNickname(),
                auth.getProvider(),
                token,
                isNewUser
        );
    }

    /**
     * ✅ 카카오 로그인
     */
    @Transactional
    public SocialLoginResponse kakaoLogin(SocialLoginRequest request) {
        OAuthUserInfo userInfo =
                oAuthUserInfoProvider.getUserInfo(ProviderType.KAKAO, request.accessToken());

        if (userInfo == null || userInfo.email() == null || userInfo.email().isBlank()) {
            throw new IllegalArgumentException("유효한 이메일을 제공하지 않는 소셜 계정입니다.");
        }

        var existingAuth = authRepository.findByEmail(userInfo.email());

        Auth auth = existingAuth.orElseGet(() ->
                authRepository.save(new Auth(userInfo.email(), userInfo.nickname(), ProviderType.KAKAO))
        );

        boolean isNewUser = existingAuth.isEmpty();

        User user = userRepository.findByKakaoId(userInfo.providerId()).orElseGet(() -> {
            User newUser = User.builder()
                    .kakaoId(userInfo.providerId())
                    .email(userInfo.email())
                    .nickname(userInfo.nickname())
                    .provider(auth.getProvider())
                    .auth(auth)
                    .build();
            return userRepository.save(newUser);
        });

        // ✅ JWT는 Auth 기준으로 발급
        String token = jwtProvider.generateToken(auth.getId(), auth.getEmail());

        return SocialLoginResponse.from(user, token, isNewUser);
    }

    /**
     * ✅ OAuth2 로그인 (SecurityConfig successHandler에서 호출)
     * 이메일 기반으로 Auth/User 저장 또는 조회 후 User 반환
     */
    @Transactional
    public User saveOrUpdate(String email, String nickname, ProviderType provider, String providerId) {
        log.info("🔎 saveOrUpdate called with email={}, nickname={}, provider={}, providerId={}",
                email, nickname, provider, providerId);

        Auth authEntity = authRepository.findByEmail(email)
                .orElseGet(() -> {
                    log.info("✅ 신규 Auth 생성 -> email={}", email);
                    Auth newAuth = Auth.builder()
                            .email(email)
                            .nickname(nickname)
                            .provider(provider)
                            .build();
                    return authRepository.saveAndFlush(newAuth);
                });

        User userEntity = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    log.info("✅ 신규 User 생성 -> email={}", email);
                    User newUser = User.builder()
                            .email(email)
                            .nickname(nickname)
                            .auth(authEntity)
                            .provider(provider)
                            .kakaoId(provider == ProviderType.KAKAO ? providerId : null)
                            .build();
                    User saved = userRepository.saveAndFlush(newUser); // flush 강제 실행
                    log.info("✅ User 저장 완료 -> authId={}, email={}", authEntity.getId(), saved.getEmail());
                    return saved;
                });

        // ✅ JWT 발급 (Auth 기준)
        String jwtToken = jwtProvider.generateToken(authEntity.getId(), authEntity.getEmail());
        log.info("✅ JWT 발급 완료 -> token={}", jwtToken);

        return userEntity;
    }
}
