package com.knu.storyboard.auth.domain;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.knu.storyboard.auth.exception.OAuthBadRequestException;

public record OAuthState(DeviceType deviceType, String redirectUrl) {

    private static final String DELIMITER = "|";

    public static OAuthState of(DeviceType deviceType, String redirectUrl) {
        if (deviceType == null) {
            throw new OAuthBadRequestException("state값이 없습니다.");
        }
        return new OAuthState(deviceType, redirectUrl);
    }

    public static OAuthState fromStateParameter(String state) {
        if (state == null || state.isEmpty()) {
            throw new OAuthBadRequestException("state값이 없습니다.");
        }

        String[] parts = state.split("\\" + DELIMITER, 2);
        DeviceType deviceType = DeviceType.fromString(parts[0]);

        if (parts.length == 1) {
            return new OAuthState(deviceType, null);
        }

        String encodedRedirectUrl = parts[1];
        String decodedRedirectUrl = encodedRedirectUrl.isEmpty()
                ? null
                : URLDecoder.decode(encodedRedirectUrl, StandardCharsets.UTF_8);

        return new OAuthState(deviceType, decodedRedirectUrl);
    }

    public String toStateParameter() {
        String encodedRedirectUrl = redirectUrl == null || redirectUrl.isBlank()
                ? ""
                : URLEncoder.encode(redirectUrl, StandardCharsets.UTF_8);

        return deviceType.name() + DELIMITER + encodedRedirectUrl;
    }
}