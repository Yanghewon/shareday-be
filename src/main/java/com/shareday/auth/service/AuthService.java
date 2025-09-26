package com.shareday.auth.service;

import com.shareday.auth.dto.OAuthUserInfo;
import com.shareday.auth.dto.SocialSignUpRequest;
import com.shareday.auth.dto.SocialSignUpResponse;
import com.shareday.auth.entity.Auth;                  // ✅ User → Auth
import com.shareday.auth.oauth.JwtProvider;
import com.shareday.auth.oauth.OAuthUserInfoProvider;
import com.shareday.auth.repository.AuthRepository;   // ✅ UserRepository → AuthRepository
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final OAuthUserInfoProvider oAuthUserInfoProvider;
    private final AuthRepository authRepository;      // ✅ UserRepository → AuthRepository
    private final JwtProvider jwtProvider;

    public AuthService(OAuthUserInfoProvider oAuthUserInfoProvider,
                       AuthRepository authRepository,   // ✅ 수정
                       JwtProvider jwtProvider) {
        this.oAuthUserInfoProvider = oAuthUserInfoProvider;
        this.authRepository = authRepository;         // ✅ 수정
        this.jwtProvider = jwtProvider;
    }

    @Transactional
    public SocialSignUpResponse socialSignUp(SocialSignUpRequest request) {
        // 1. provider에서 사용자 정보 조회
        OAuthUserInfo userInfo =
                oAuthUserInfoProvider.getUserInfo(request.provider(), request.accessToken());

        // 2. 사용자 존재 여부 확인
        Auth auth = authRepository.findByEmail(userInfo.email())
                .orElseGet(() -> {
                    Auth newAuth = Auth.builder()
                            .email(userInfo.email())
                            .nickname(userInfo.nickname())
                            .provider(userInfo.provider())
                            .build();
                    return authRepository.save(newAuth);
                });

        boolean isNewUser = auth.getCreatedAt().equals(auth.getUpdatedAt());

        // 3. JWT 발급
        String token = jwtProvider.generateToken(auth.getId(), auth.getEmail());

        return new SocialSignUpResponse(
                auth.getId(),
                auth.getEmail(),
                auth.getNickname(),
                userInfo.provider(),
                token,
                isNewUser
        );
    }
}
