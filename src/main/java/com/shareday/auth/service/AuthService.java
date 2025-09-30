package com.shareday.auth.service;

import com.shareday.auth.dto.OAuthUserInfo;
import com.shareday.auth.dto.SocialSignUpRequest;
import com.shareday.auth.dto.SocialSignUpResponse;
import com.shareday.auth.entity.Auth;
import com.shareday.auth.enums.ProviderType;
import com.shareday.auth.oauth.JwtProvider;
import com.shareday.auth.oauth.OAuthUserInfoProvider;
import com.shareday.auth.repository.AuthRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final OAuthUserInfoProvider oAuthUserInfoProvider;
    private final AuthRepository authRepository;
    private final JwtProvider jwtProvider;

    public AuthService(OAuthUserInfoProvider oAuthUserInfoProvider,
                       AuthRepository authRepository,
                       JwtProvider jwtProvider) {
        this.oAuthUserInfoProvider = oAuthUserInfoProvider;
        this.authRepository = authRepository;
        this.jwtProvider = jwtProvider;
    }

    @Transactional
    public SocialSignUpResponse socialSignUp(SocialSignUpRequest request) {
        OAuthUserInfo userInfo =
                oAuthUserInfoProvider.getUserInfo(request.provider(), request.accessToken());

        if (userInfo == null || userInfo.email() == null || userInfo.email().isBlank()) {
            throw new IllegalArgumentException("유효한 이메일을 제공하지 않는 소셜 계정입니다.");
        }

        var existing = authRepository.findByEmail(userInfo.email());

        Auth auth = existing.orElseGet(() -> {
            // userInfo.provider()가 이미 ProviderType이면 그대로 사용
            ProviderType provider = (userInfo.provider() instanceof ProviderType p)
                    ? p
                    : ProviderType.valueOf(userInfo.provider().toString().toUpperCase());
            return authRepository.save(new Auth(userInfo.email(), userInfo.nickname(), provider));
        });

        boolean isNewUser = existing.isEmpty();

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
}
