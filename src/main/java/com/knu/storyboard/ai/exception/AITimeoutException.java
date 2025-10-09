package com.knu.storyboard.ai.exception;

public class AITimeoutException extends RuntimeException {
    public AITimeoutException(String message) {
        super(message);
    }

    public AITimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}