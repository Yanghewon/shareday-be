package com.shareday.auth.dto;

import com.shareday.user.entity.User;
import lombok.Builder;

@Builder
public record SocialLoginResponse(
        Long userId,
        String email,
        String nickname,
        String jwtToken,
        boolean isNewUser
) {
    public static SocialLoginResponse from(User user, String token, boolean isNewUser) {
        return SocialLoginResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .jwtToken(token)
                .isNewUser(isNewUser)
                .build();
    }
}
