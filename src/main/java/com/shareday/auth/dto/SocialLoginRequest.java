package com.shareday.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record SocialLoginRequest(
        @NotBlank(message = "AccessToken은 필수입니다.")
        String accessToken
) { }
