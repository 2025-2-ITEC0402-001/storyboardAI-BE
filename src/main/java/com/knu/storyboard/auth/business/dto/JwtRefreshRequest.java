package com.knu.storyboard.auth.business.dto;

public record JwtRefreshRequest(
        String refreshToken,
        String deviceType
) {
}
