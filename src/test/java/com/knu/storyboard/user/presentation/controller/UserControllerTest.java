package com.knu.storyboard.user.presentation.controller;

import com.knu.storyboard.auth.business.dto.JwtRefreshRequest;
import com.knu.storyboard.auth.business.dto.JwtResponse;
import com.knu.storyboard.auth.business.service.JwtService;
import com.knu.storyboard.auth.domain.AuthUser;
import com.knu.storyboard.user.business.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserController userController;

    @Test
    @DisplayName("JWT 리프레쉬 요청 시 새로운 토큰을 반환한다")
    void refreshToken_ShouldReturnNewTokens() {
        // Given
        JwtRefreshRequest request = new JwtRefreshRequest("refresh-token", "COMPUTER");
        JwtResponse expectedResponse = new JwtResponse("new-access-token", "new-refresh-token");
        
        when(jwtService.refreshAccessToken(request)).thenReturn(expectedResponse);

        // When
        ResponseEntity<JwtResponse> response = userController.refreshToken(request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
        verify(jwtService, times(1)).refreshAccessToken(request);
    }

    @Test
    @DisplayName("로그아웃 요청 시 토큰을 무효화한다")
    void logout_ShouldInvalidateToken() {
        // Given
        UUID userId = UUID.randomUUID();
        String deviceType = "COMPUTER";

        // When
        ResponseEntity<Void> response = userController.logout(deviceType, userId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(jwtService, times(1)).logout(userId, deviceType);
    }
}
