package com.knu.storyboard.auth.business.dto;

public record JwtResponse(
        String accessToken,
        String refreshToken
) {
}
