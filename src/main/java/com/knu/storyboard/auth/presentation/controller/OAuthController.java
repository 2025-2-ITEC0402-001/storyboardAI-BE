package com.knu.storyboard.auth.presentation.controller;

import java.net.URI;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.knu.storyboard.auth.business.service.OAuthLoginService;
import com.knu.storyboard.auth.domain.DeviceType;
import com.knu.storyboard.auth.domain.OAuthState;
import com.knu.storyboard.auth.presentation.api.OAuthApi;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OAuthController implements OAuthApi {

    private final OAuthLoginService oAuthLoginService;

    @Override
    public RedirectView redirectToOAuthLoginPage(
            @PathVariable("provider") String provider,
            @RequestParam(value = "deviceType", required = false) String deviceType,
            @RequestParam(value = "redirectUrl", required = false) String redirectUrl) {

        String state = OAuthState.of(DeviceType.fromString(Optional.ofNullable(deviceType).orElse(DeviceType.COMPUTER.name())), redirectUrl)
                .toStateParameter();

        String redirectViewUrl = oAuthLoginService.getOAuthLoginUrl(provider, state);
        return new RedirectView(redirectViewUrl);
    }

    @Override
    public ResponseEntity<Void> handleOAuthCallback(
            @PathVariable("provider") String provider,
            @RequestParam("code") String code,
            @RequestParam(value = "state") String state) {

        URI redirectInfo = oAuthLoginService.handleOAuthCallback(provider, code, state);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(redirectInfo);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
