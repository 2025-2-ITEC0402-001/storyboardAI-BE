package com.knu.storyboard.user.domain;

import com.knu.storyboard.user.business.dto.UserEntityDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserDomainTest {

    @Test
    @DisplayName("사용자 도메인을 생성할 수 있다")
    void create_ShouldCreateUserDomain() {
        // Given
        UUID id = UUID.randomUUID();
        String email = "test@example.com";
        String nickname = "테스트유저";
        String status = "ACTIVE";

        // When
        UserDomain userDomain = UserDomain.create(id, email, nickname, status);

        // Then
        assertThat(userDomain.getId()).isEqualTo(id);
        assertThat(userDomain.getEmail()).isEqualTo(email);
        assertThat(userDomain.getNickname()).isEqualTo(nickname);
        assertThat(userDomain.getStatus()).isEqualTo(status);
    }

    @Test
    @DisplayName("활성 상태 사용자는 탈퇴하지 않은 상태이다")
    void isWithdrawn_ShouldReturnFalse_WhenUserIsActive() {
        // Given
        UserDomain activeUser = UserDomain.create(
                UUID.randomUUID(),
                "test@example.com",
                "테스트유저",
                UserStatus.ACTIVE.name()
        );

        // When
        boolean isWithdrawn = activeUser.isWithdrawn();

        // Then
        assertThat(isWithdrawn).isFalse();
    }

    @Test
    @DisplayName("탈퇴 상태 사용자는 탈퇴한 상태이다")
    void isWithdrawn_ShouldReturnTrue_WhenUserIsWithdrawn() {
        // Given
        UserDomain withdrawnUser = UserDomain.create(
                UUID.randomUUID(),
                "test@example.com",
                "테스트유저",
                UserStatus.WITHDRAWN.name()
        );

        // When
        boolean isWithdrawn = withdrawnUser.isWithdrawn();

        // Then
        assertThat(isWithdrawn).isTrue();
    }

    @Test
    @DisplayName("비활성 상태 사용자는 비활성 상태이다")
    void isInactive_ShouldReturnTrue_WhenUserIsInactive() {
        // Given
        UserDomain inactiveUser = UserDomain.create(
                UUID.randomUUID(),
                "test@example.com",
                "테스트유저",
                UserStatus.INACTIVE.name()
        );

        // When
        boolean isInactive = inactiveUser.isInactive();

        // Then
        assertThat(isInactive).isTrue();
    }

    @Test
    @DisplayName("활성 상태 사용자는 비활성 상태가 아니다")
    void isInactive_ShouldReturnFalse_WhenUserIsActive() {
        // Given
        UserDomain activeUser = UserDomain.create(
                UUID.randomUUID(),
                "test@example.com",
                "테스트유저",
                UserStatus.ACTIVE.name()
        );

        // When
        boolean isInactive = activeUser.isInactive();

        // Then
        assertThat(isInactive).isFalse();
    }
}
