package com.knu.storyboard.auth.domain;

import com.knu.storyboard.auth.exception.OAuthErrorException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OAuthTokenTest {

    @Test
    @DisplayName("카카오 OAuth 토큰을 생성할 수 있다")
    void ofKakao_ShouldCreateKakaoOAuthToken() {
        // Given
        String accessToken = "kakao-access-token";
        String refreshToken = "kakao-refresh-token";
        Long expiresIn = 3600L;

        // When
        OAuthToken token = OAuthToken.ofKakao(accessToken, refreshToken, expiresIn);

        // Then
        assertThat(token.getProvider()).isEqualTo(OAuthProvider.KAKAO);
        assertThat(token.getAccessToken()).isEqualTo(accessToken);
        assertThat(token.getRefreshToken()).isEqualTo(refreshToken);
        assertThat(token.getExpiresIn()).isEqualTo(expiresIn);
        assertThat(token.getIssuedAt()).isBefore(LocalDateTime.now().plusSeconds(1));
        assertThat(token.getIssuedAt()).isAfter(LocalDateTime.now().minusSeconds(1));
    }

    @Test
    @DisplayName("카카오 OAuth 토큰 생성 시 accessToken이 null이면 예외가 발생한다")
    void ofKakao_ShouldThrowException_WhenAccessTokenIsNull() {
        // Given
        String accessToken = null;
        String refreshToken = "refresh-token";
        Long expiresIn = 3600L;

        // When & Then
        assertThatThrownBy(() -> OAuthToken.ofKakao(accessToken, refreshToken, expiresIn))
                .isInstanceOf(OAuthErrorException.class)
                .hasMessage("응답이 올바르지 않아 accessToken이 전달되지 않았습니다.");
    }

    @Test
    @DisplayName("일반 OAuth 토큰을 생성할 수 있다")
    void create_ShouldCreateOAuthToken() {
        // Given
        OAuthProvider provider = OAuthProvider.KAKAO;
        String accessToken = "access-token";
        String refreshToken = "refresh-token";
        Long expiresIn = 7200L;

        // When
        OAuthToken token = OAuthToken.create(provider, accessToken, refreshToken, expiresIn);

        // Then
        assertThat(token.getProvider()).isEqualTo(provider);
        assertThat(token.getAccessToken()).isEqualTo(accessToken);
        assertThat(token.getRefreshToken()).isEqualTo(refreshToken);
        assertThat(token.getExpiresIn()).isEqualTo(expiresIn);
        assertThat(token.getIssuedAt()).isBefore(LocalDateTime.now().plusSeconds(1));
        assertThat(token.getIssuedAt()).isAfter(LocalDateTime.now().minusSeconds(1));
    }

    @Test
    @DisplayName("accessToken이 null이면 null을 반환한다")
    void create_ShouldReturnNull_WhenAccessTokenIsNull() {
        // Given
        OAuthProvider provider = OAuthProvider.KAKAO;
        String accessToken = null;
        String refreshToken = "refresh-token";
        Long expiresIn = 3600L;

        // When
        OAuthToken token = OAuthToken.create(provider, accessToken, refreshToken, expiresIn);

        // Then
        assertThat(token).isNull();
    }

    @Test
    @DisplayName("refreshToken이 null이어도 토큰을 생성할 수 있다")
    void create_ShouldCreateToken_WhenRefreshTokenIsNull() {
        // Given
        OAuthProvider provider = OAuthProvider.KAKAO;
        String accessToken = "access-token";
        String refreshToken = null;
        Long expiresIn = 3600L;

        // When
        OAuthToken token = OAuthToken.create(provider, accessToken, refreshToken, expiresIn);

        // Then
        assertThat(token.getAccessToken()).isEqualTo(accessToken);
        assertThat(token.getRefreshToken()).isNull();
        assertThat(token.getProvider()).isEqualTo(provider);
    }
}