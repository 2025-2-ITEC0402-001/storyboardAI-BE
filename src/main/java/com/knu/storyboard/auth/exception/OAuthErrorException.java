package com.knu.storyboard.auth.exception;

public class OAuthErrorException extends RuntimeException {
    public OAuthErrorException(String message) {
        super(message);
    }
}
