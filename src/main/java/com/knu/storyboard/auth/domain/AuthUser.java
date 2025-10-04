package com.knu.storyboard.auth.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class AuthUser {
    private UUID id;

    public static AuthUser from(Token token) {
        return new AuthUser(UUID.fromString(token.getSubject()));
    }
}
