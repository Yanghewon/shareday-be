package com.shareday.auth.oauth;

import com.shareday.auth.dto.OAuthUserInfo;
import com.shareday.auth.enums.ProviderType;

public interface OAuthUserInfoProvider {
    OAuthUserInfo getUserInfo(ProviderType provider, String accessToken);
}