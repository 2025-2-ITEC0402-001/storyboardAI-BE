package com.knu.storyboard.auth.exception;

public class TokenStolenException extends RuntimeException {
    public TokenStolenException(String message) {
        super(message);
    }
}
