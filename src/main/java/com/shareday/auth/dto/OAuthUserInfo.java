package com.shareday.auth.dto;

import com.shareday.auth.enums.ProviderType;

public record OAuthUserInfo(
        ProviderType provider,
        String providerId, // provider에서 주는 유니크 아이디
        String email,
        String nickname
) {}