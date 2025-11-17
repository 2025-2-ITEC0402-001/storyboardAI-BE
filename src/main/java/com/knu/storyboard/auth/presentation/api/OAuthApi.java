package com.knu.storyboard.auth.presentation.api;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping("/api/auth/oauth")
@Tag(name = "OAuth", description = "OAuth 관련 API")
public interface OAuthApi {

    @GetMapping("/{provider}/login")
    @Operation(summary = "OAuth 로그인 페이지 이동",
               description = "provider별 로그인 페이지로 이동한다. deviceType(COMPUTER/PHONE)과 redirectUrl을 전달할 수 있다.")
    RedirectView redirectToOAuthLoginPage(
            @PathVariable("provider") String provider,
            @RequestParam(value = "deviceType", required = false) String deviceType,
            @RequestParam(value = "redirectUrl", required = false) String redirectUrl);

    @GetMapping("/{provider}/callback")
    @Operation(summary = "OAuth용 콜백 [직접 사용 금지]",
            description = "provider 측에서 콜백 리다이렉트로 사용할 엔드포인트. 직접 사용 금지.")
    ResponseEntity<Void> handleOAuthCallback(
            @PathVariable("provider") String provider,
            @RequestParam("code") String code,
            @RequestParam(value = "state") String state);
}
