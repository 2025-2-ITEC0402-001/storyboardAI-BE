package com.knu.storyboard.auth.business.dto;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PROTECTED)
public record OAuthLoginResponse(
        String accessToken,
        String refreshToken
) {
    public static OAuthLoginResponse toJwt(String accessToken, String refreshToken) {
        return OAuthLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
