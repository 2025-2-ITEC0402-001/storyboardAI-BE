package com.knu.storyboard.user.presentation.controller;

import com.knu.storyboard.auth.business.dto.JwtRefreshRequest;
import com.knu.storyboard.auth.business.dto.JwtResponse;
import com.knu.storyboard.auth.business.service.JwtService;
import com.knu.storyboard.auth.domain.AuthUser;
import com.knu.storyboard.auth.presentation.annotation.Login;
import com.knu.storyboard.auth.presentation.annotation.RequireAuth;
import com.knu.storyboard.user.business.dto.DummyRequest;
import com.knu.storyboard.user.business.service.UserService;
import com.knu.storyboard.user.presentation.api.UserApi;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;
    private final JwtService jwtService;

    @Override
    public ResponseEntity<JwtResponse> dummyLogin(DummyRequest dummyRequest) {
        JwtResponse jwtResponse = userService.dummyLogin(dummyRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @Override
    public ResponseEntity<JwtResponse> refreshToken(JwtRefreshRequest request) {
        JwtResponse jwtResponse = jwtService.refreshAccessToken(request);
        return ResponseEntity.ok(jwtResponse);
    }

    @Override
    public ResponseEntity<Void> logout(String deviceType, UUID userId) {
        jwtService.logout(userId, deviceType);
        return ResponseEntity.ok().build();
    }

    @Override
    @RequireAuth
    public ResponseEntity<Void> deleteAccount(@Parameter(hidden = true) @Login AuthUser authUser) {
        jwtService.logout(authUser.getId(), "COMPUTER");
        jwtService.logout(authUser.getId(), "PHONE");

        userService.deleteUser(authUser.getId());

        return ResponseEntity.ok().build();
    }
}
