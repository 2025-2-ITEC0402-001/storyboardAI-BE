package com.knu.storyboard.auth.exception;

public class TokenBadRequestException extends RuntimeException {
    public TokenBadRequestException(String message) {
        super(message);
    }
}
