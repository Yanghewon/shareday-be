package com.shareday.auth.authService;

import com.shareday.auth.dto.OAuthUserInfo;
import com.shareday.auth.dto.SocialSignUpRequest;
import com.shareday.auth.dto.SocialSignUpResponse;
import com.shareday.auth.entity.User;
import com.shareday.auth.oauth.JwtProvider;
import com.shareday.auth.oauth.OAuthUserInfoProvider;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final OAuthUserInfoProvider oAuthUserInfoProvider;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public AuthService(OAuthUserInfoProvider oAuthUserInfoProvider,
                       UserRepository userRepository,
                       JwtProvider jwtProvider) {
        this.oAuthUserInfoProvider = oAuthUserInfoProvider;
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }

    @Transactional
    public SocialSignUpResponse socialSignUp(SocialSignUpRequest request) {
        // 1. provider에서 사용자 정보 조회
        OAuthUserInfo userInfo =
                oAuthUserInfoProvider.getUserInfo(request.provider(), request.accessToken());

        // 2. 사용자 존재 여부 확인
        User user = userRepository.findByEmail(userInfo.email())
                .orElseGet(() -> {
                    User newUser = new User(userInfo.email(), userInfo.nickname(), userInfo.provider());
                    return userRepository.save(newUser);
                });

        boolean isNewUser = user.getCreatedAt().equals(user.getUpdatedAt());

        // 3. JWT 발급
        String token = jwtProvider.generateToken(user.getId(), user.getEmail());

        return new SocialSignUpResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                userInfo.provider(),
                token,
                isNewUser
        );
    }
}