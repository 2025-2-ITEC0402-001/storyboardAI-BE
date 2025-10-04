package com.knu.storyboard.user.presentation.api;

import com.knu.storyboard.auth.business.dto.JwtRefreshRequest;
import com.knu.storyboard.auth.business.dto.JwtResponse;
import com.knu.storyboard.auth.domain.AuthUser;
import com.knu.storyboard.auth.presentation.annotation.Login;
import com.knu.storyboard.user.business.dto.DummyRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "유저", description = "유저 관련 API")
@RequestMapping("/api/user")
public interface UserApi {

    @PostMapping("/dummy")
    @Operation(summary = "[테스트용] 임시 로그인용 JWT 생성", description = "테스트를 위해 OAuth를 거치지 않고도 서버에서 사용 가능한 인증용 JWT를 생성한다.")
    ResponseEntity<JwtResponse> dummyLogin(
            @RequestBody DummyRequest dummyRequest);

    @PostMapping("/refresh")
    @Operation(summary = "JWT 토큰 리프레쉬", description = "Refresh Token을 사용하여 새로운 Access Token을 발급받습니다.")
    ResponseEntity<JwtResponse> refreshToken(@RequestBody JwtRefreshRequest request);

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그아웃하여 저장된 토큰을 무효화합니다.")
    ResponseEntity<Void> logout(
            @RequestParam String deviceType,
            @RequestParam UUID userId);

    @DeleteMapping("/withdraw")
    @Operation(summary = "회원 탈퇴", description = "회원 정보를 완전히 삭제합니다.")
    ResponseEntity<Void> deleteAccount(@Parameter(hidden = true) @Login AuthUser authUser);
}
