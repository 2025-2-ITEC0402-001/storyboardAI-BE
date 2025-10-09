package com.knu.storyboard.ai.exception;

public class AIBadRequestException extends RuntimeException {
    public AIBadRequestException(String message) {
        super(message);
    }

    public AIBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}