package com.knu.storyboard.auth.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthUserTest {

    @Mock
    private Token mockToken;

    @Test
    @DisplayName("토큰으로부터 AuthUser를 생성할 수 있다")
    void from_ShouldCreateAuthUserFromToken() {
        // Given
        UUID expectedUserId = UUID.randomUUID();
        when(mockToken.getSubject()).thenReturn(expectedUserId.toString());

        // When
        AuthUser authUser = AuthUser.from(mockToken);

        // Then
        assertThat(authUser.getId()).isEqualTo(expectedUserId);
    }

    @Test
    @DisplayName("Builder 패턴으로 AuthUser를 생성할 수 있다")
    void builder_ShouldCreateAuthUser() {
        // Given
        UUID userId = UUID.randomUUID();

        // When
        AuthUser authUser = AuthUser.builder()
                .id(userId)
                .build();

        // Then
        assertThat(authUser.getId()).isEqualTo(userId);
    }
}