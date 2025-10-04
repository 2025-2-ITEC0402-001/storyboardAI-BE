package com.knu.storyboard.user.business.service;

import com.knu.storyboard.user.domain.User;
import com.knu.storyboard.user.domain.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserFactoryTest {

    @Test
    @DisplayName("사용자를 생성할 수 있다")
    void create_ShouldCreateUser() {
        // Given
        UUID id = UUID.randomUUID();
        String email = "test@example.com";
        String nickname = "테스트유저";
        String status = UserStatus.ACTIVE.name();

        // When
        User user = UserFactory.create(id, email, nickname, status);

        // Then
        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getNickname()).isEqualTo(nickname);
    }

    @Test
    @DisplayName("이메일로부터 닉네임을 생성할 수 있다")
    void generateNicknameFromEmail_ShouldReturnNickname() {
        // Given
        String email = "testuser@example.com";

        // When
        String nickname = UserFactory.generateNicknameFromEmail(email);

        // Then
        assertThat(nickname).isEqualTo("testuser");
    }

    @Test
    @DisplayName("특수문자가 포함된 이메일에서도 닉네임을 생성할 수 있다")
    void generateNicknameFromEmail_ShouldHandleSpecialCharacters() {
        // Given
        String email = "test.user+123@example.com";

        // When
        String nickname = UserFactory.generateNicknameFromEmail(email);

        // Then
        assertThat(nickname).isEqualTo("test.user+123");
    }

    @Test
    @DisplayName("null 이메일로 닉네임 생성 시 예외가 발생한다")
    void generateNicknameFromEmail_ShouldThrowException_WhenEmailIsNull() {
        // Given
        String email = null;

        // When & Then
        assertThatThrownBy(() -> UserFactory.generateNicknameFromEmail(email))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 이메일 형식입니다.");
    }

    @Test
    @DisplayName("@ 기호가 없는 이메일로 닉네임 생성 시 예외가 발생한다")
    void generateNicknameFromEmail_ShouldThrowException_WhenEmailHasNoAtSign() {
        // Given
        String email = "invalidEmail";

        // When & Then
        assertThatThrownBy(() -> UserFactory.generateNicknameFromEmail(email))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 이메일 형식입니다.");
    }
}