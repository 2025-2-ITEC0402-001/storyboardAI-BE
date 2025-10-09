package com.knu.storyboard.project.exception;

public class ProjectBadRequestException extends RuntimeException {
    public ProjectBadRequestException(String message) {
        super(message);
    }

    public ProjectBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}