package com.knu.storyboard.auth.business.dto;

import com.knu.storyboard.auth.domain.DeviceType;
import com.knu.storyboard.auth.domain.OAuthProvider;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PROTECTED)
public record OAuthContext(OAuthProvider oAuthProvider, DeviceType deviceType, String redirectUrl) {
    public static OAuthContext of(OAuthProvider oAuthProvider, DeviceType deviceType, String redirectUrl) {
        return OAuthContext.builder()
                .oAuthProvider(oAuthProvider)
                .deviceType(deviceType)
                .redirectUrl(redirectUrl)
                .build();
    }
}
