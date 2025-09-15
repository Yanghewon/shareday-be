package com.shareday.auth.dto;

import com.shareday.auth.enums.ProviderType;
import com.shareday.common.annotaion.ValidEnum;
import jakarta.validation.constraints.NotBlank;

public record SocialSignUpRequest(
        @NotBlank(message = "provider는 필수입니다.")
        @ValidEnum(enumClass = ProviderType.class, message = "provider 값이 올바르지 않습니다.")
        ProviderType provider,

        @NotBlank(message = "accessToken은 필수입니다.")
        String accessToken // 또는 authorizationCode
) { }
