package com.shareday.user.dto;

import com.shareday.user.entity.User;
import com.shareday.auth.enums.ProviderType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String nickname;
    private String kakaoId;
    private ProviderType provider;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .kakaoId(user.getKakaoId())
                .provider(user.getProvider())
                .build();
    }
}
