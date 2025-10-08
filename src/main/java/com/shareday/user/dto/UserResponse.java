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
    private ProviderType provider;
    private boolean coupled; // ✅ 커플 여부 필드 추가

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .provider(user.getProvider())
                .coupled(user.getCouple() != null) // ✅ 커플 객체가 있으면 true
                .build();
    }
}

