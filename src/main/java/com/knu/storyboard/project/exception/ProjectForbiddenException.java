package com.knu.storyboard.project.exception;

public class ProjectForbiddenException extends RuntimeException {
    public ProjectForbiddenException(String message) {
        super(message);
    }

    public ProjectForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}