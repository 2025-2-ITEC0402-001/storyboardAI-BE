package com.knu.storyboard.auth.domain;

import com.knu.storyboard.auth.business.dto.TokenDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TokenTest {

    @Test
    @DisplayName("토큰을 생성할 수 있다")
    void of_ShouldCreateToken() {
        // Given
        TokenType type = TokenType.ACCESS;
        String value = "test-token";
        String subject = UUID.randomUUID().toString();
        Date issueAt = new Date();
        Date expiration = new Date(System.currentTimeMillis() + 3600000); // 1시간 후

        // When
        Token token = Token.of(type, value, subject, issueAt, expiration);

        // Then
        assertThat(token.getType()).isEqualTo(type);
        assertThat(token.getValue()).isEqualTo(value);
        assertThat(token.getSubject()).isEqualTo(subject);
        assertThat(token.getIssueAt()).isEqualTo(issueAt);
        assertThat(token.getExpiration()).isEqualTo(expiration);
    }

    @Test
    @DisplayName("만료되지 않은 토큰은 유효하다")
    void isExpired_ShouldReturnFalse_WhenTokenIsNotExpired() {
        // Given
        Date futureExpiration = new Date(System.currentTimeMillis() + 3600000); // 1시간 후
        Token token = Token.of(
                TokenType.ACCESS,
                "test-token",
                UUID.randomUUID().toString(),
                new Date(),
                futureExpiration
        );

        // When
        boolean isExpired = token.isExpired();

        // Then
        assertThat(isExpired).isFalse();
    }

    @Test
    @DisplayName("만료된 토큰은 유효하지 않다")
    void isExpired_ShouldReturnTrue_WhenTokenIsExpired() {
        // Given
        Date pastExpiration = new Date(System.currentTimeMillis() - 3600000); // 1시간 전
        Token token = Token.of(
                TokenType.ACCESS,
                "test-token",
                UUID.randomUUID().toString(),
                new Date(),
                pastExpiration
        );

        // When
        boolean isExpired = token.isExpired();

        // Then
        assertThat(isExpired).isTrue();
    }

    @Test
    @DisplayName("Access Token 타입을 올바르게 판별한다")
    void isAccessToken_ShouldReturnTrue_WhenTokenIsAccessType() {
        // Given
        Token accessToken = Token.of(
                TokenType.ACCESS,
                "access-token",
                UUID.randomUUID().toString(),
                new Date(),
                new Date(System.currentTimeMillis() + 3600000)
        );

        // When
        boolean isAccessToken = accessToken.isAccessToken();

        // Then
        assertThat(isAccessToken).isTrue();
    }

    @Test
    @DisplayName("Refresh Token 타입을 올바르게 판별한다")
    void isRefreshToken_ShouldReturnTrue_WhenTokenIsRefreshType() {
        // Given
        Token refreshToken = Token.of(
                TokenType.REFRESH,
                "refresh-token",
                UUID.randomUUID().toString(),
                new Date(),
                new Date(System.currentTimeMillis() + 7200000)
        );

        // When
        boolean isRefreshToken = refreshToken.isRefreshToken();

        // Then
        assertThat(isRefreshToken).isTrue();
    }

    @Test
    @DisplayName("같은 값의 토큰인지 확인할 수 있다")
    void isSameValue_ShouldReturnTrue_WhenValuesAreEqual() {
        // Given
        String tokenValue = "test-token-value";
        Token token = Token.of(
                TokenType.ACCESS,
                tokenValue,
                UUID.randomUUID().toString(),
                new Date(),
                new Date(System.currentTimeMillis() + 3600000)
        );

        // When
        boolean isSameValue = token.isSameValue(tokenValue);

        // Then
        assertThat(isSameValue).isTrue();
    }

    @Test
    @DisplayName("다른 값의 토큰인지 확인할 수 있다")
    void isSameValue_ShouldReturnFalse_WhenValuesAreDifferent() {
        // Given
        Token token = Token.of(
                TokenType.ACCESS,
                "original-token-value",
                UUID.randomUUID().toString(),
                new Date(),
                new Date(System.currentTimeMillis() + 3600000)
        );

        // When
        boolean isSameValue = token.isSameValue("different-token-value");

        // Then
        assertThat(isSameValue).isFalse();
    }

    @Test
    @DisplayName("TokenDTO로 변환할 수 있다")
    void toTokenDTO_ShouldReturnCorrectTokenDTO() {
        // Given
        String tokenValue = "test-token-value";
        Token token = Token.of(
                TokenType.ACCESS,
                tokenValue,
                UUID.randomUUID().toString(),
                new Date(),
                new Date(System.currentTimeMillis() + 3600000)
        );

        // When
        TokenDTO tokenDTO = token.toTokenDTO();

        // Then
        assertThat(tokenDTO.value()).isEqualTo(tokenValue);
    }
}