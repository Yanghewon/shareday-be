package com.shareday.auth.dto;

import com.shareday.auth.enums.ProviderType;

public record SocialSignUpResponse(
        Long userId,
        String email,
        String nickname,
        ProviderType provider,
        String jwtToken,
        boolean isNewUser
) { }
