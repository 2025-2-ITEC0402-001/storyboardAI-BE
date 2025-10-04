package com.knu.storyboard.user.business.service;

import com.knu.storyboard.auth.business.dto.JwtResponse;
import com.knu.storyboard.auth.business.port.OAuthRepository;
import com.knu.storyboard.auth.business.service.OAuthLoginService;
import com.knu.storyboard.auth.domain.DeviceType;
import com.knu.storyboard.user.business.dto.DummyRequest;
import com.knu.storyboard.user.business.dto.UserEntityDto;
import com.knu.storyboard.user.business.port.UserRepository;
import com.knu.storyboard.user.domain.UserStatus;
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
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OAuthRepository oAuthRepository;

    @Mock
    private OAuthLoginService oAuthLoginService;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("회원 탈퇴 시 OAuth 매핑과 사용자 정보가 모두 삭제된다")
    void deleteUser_ShouldDeleteOAuthMappingAndUser() {
        // Given
        UUID userId = UUID.randomUUID();

        // When
        userService.deleteUser(userId);

        // Then
        verify(oAuthRepository, times(1)).deleteByUserId(userId);
        verify(userRepository, times(1)).delete(userId);
    }

    @Test
    @DisplayName("기존 사용자가 더미 로그인을 하면 JWT를 반환한다")
    void dummyLogin_ShouldReturnJwt_WhenUserExists() {
        // Given
        String email = "test@example.com";
        DummyRequest dummyRequest = new DummyRequest(email);
        UUID userId = UUID.randomUUID();
        UserEntityDto existingUser = UserEntityDto.create(userId, email, "test", UserStatus.ACTIVE.name());
        JwtResponse expectedJwt = new JwtResponse("access-token", "refresh-token");

        when(userRepository.findOptionalByEmail(email)).thenReturn(Optional.of(existingUser));
        when(userRepository.getByEmail(email)).thenReturn(existingUser);
        when(oAuthLoginService.generateTokensForUser(userId, DeviceType.COMPUTER)).thenReturn(expectedJwt);

        // When
        JwtResponse result = userService.dummyLogin(dummyRequest);

        // Then
        assertThat(result).isEqualTo(expectedJwt);
        verify(userRepository, never()).save(any(), any(), any());
        verify(oAuthLoginService, times(1)).generateTokensForUser(userId, DeviceType.COMPUTER);
    }

    @Test
    @DisplayName("새로운 사용자가 더미 로그인을 하면 사용자를 생성하고 JWT를 반환한다")
    void dummyLogin_ShouldCreateUserAndReturnJwt_WhenUserNotExists() {
        // Given
        String email = "newuser@example.com";
        DummyRequest dummyRequest = new DummyRequest(email);
        UUID userId = UUID.randomUUID();
        String expectedNickname = "newuser";
        UserEntityDto newUser = UserEntityDto.create(userId, email, expectedNickname, UserStatus.ACTIVE.name());
        JwtResponse expectedJwt = new JwtResponse("access-token", "refresh-token");

        when(userRepository.findOptionalByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(email, expectedNickname, UserStatus.ACTIVE.name())).thenReturn(newUser);
        when(oAuthLoginService.generateTokensForUser(userId, DeviceType.COMPUTER)).thenReturn(expectedJwt);

        // When
        JwtResponse result = userService.dummyLogin(dummyRequest);

        // Then
        assertThat(result).isEqualTo(expectedJwt);
        verify(userRepository, times(1)).save(email, expectedNickname, UserStatus.ACTIVE.name());
        verify(oAuthLoginService, times(1)).generateTokensForUser(userId, DeviceType.COMPUTER);
    }
}