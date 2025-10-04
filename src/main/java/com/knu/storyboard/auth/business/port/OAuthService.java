package com.knu.storyboard.auth.business.port;

import com.knu.storyboard.auth.domain.OAuthUserInfo;

public interface OAuthService {
    OAuthUserInfo getUserInfo(String code);

    String getRedirectUrl(String state);

    boolean isBackendRedirect();
}
