package com.knu.storyboard.auth.business.service;

import com.knu.storyboard.auth.business.dto.JwtRefreshRequest;
import com.knu.storyboard.auth.business.dto.JwtResponse;
import com.knu.storyboard.auth.business.dto.TokenDTO;
import com.knu.storyboard.auth.business.port.TokenRepository;
import com.knu.storyboard.auth.business.service.validator.JwtValidator;
import com.knu.storyboard.auth.domain.Token;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private JwtFactory jwtFactory;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private JwtValidator jwtValidator;

    @Mock
    private Token mockRefreshToken;

    @Mock
    private Token mockAccessToken;

    @Mock
    private Token mockNewRefreshToken;

    @InjectMocks
    private JwtService jwtService;

    @Test
    @DisplayName("유효한 Refresh Token으로 새로운 Access Token을 발급받는다")
    void refreshAccessToken_ShouldReturnNewTokens() {
        // Given
        UUID userId = UUID.randomUUID();
        String refreshTokenValue = "valid-refresh-token";
        String deviceType = "COMPUTER";
        JwtRefreshRequest request = new JwtRefreshRequest(refreshTokenValue, deviceType);

        when(jwtFactory.parseToken(refreshTokenValue)).thenReturn(Optional.of(mockRefreshToken));
        when(mockRefreshToken.getSubject()).thenReturn(userId.toString());
        when(jwtFactory.createAccessToken(userId)).thenReturn(mockAccessToken);
        when(jwtFactory.createRefreshToken(userId)).thenReturn(mockNewRefreshToken);
        when(mockAccessToken.getValue()).thenReturn("new-access-token");
        when(mockNewRefreshToken.getValue()).thenReturn("new-refresh-token");
        when(mockNewRefreshToken.toTokenDTO()).thenReturn(mock(TokenDTO.class));

        // When
        JwtResponse result = jwtService.refreshAccessToken(request);

        // Then
        assertThat(result.accessToken()).isEqualTo("new-access-token");
        assertThat(result.refreshToken()).isEqualTo("new-refresh-token");
        verify(jwtValidator, times(1)).validateRefreshToken(mockRefreshToken, userId, deviceType);
        verify(tokenRepository, times(1)).saveToken(eq(userId), eq(deviceType), any(TokenDTO.class));
        verify(tokenRepository, times(1)).updateLastRefreshTime(userId, deviceType);
    }

    @Test
    @DisplayName("로그아웃 시 토큰이 제거된다")
    void logout_ShouldRemoveToken() {
        // Given
        UUID userId = UUID.randomUUID();
        String deviceType = "COMPUTER";

        // When
        jwtService.logout(userId, deviceType);

        // Then
        verify(tokenRepository, times(1)).removeToken(userId, deviceType);
    }
}