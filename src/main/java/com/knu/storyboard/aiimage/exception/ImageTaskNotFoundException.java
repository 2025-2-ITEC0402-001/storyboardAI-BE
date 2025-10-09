package com.knu.storyboard.aiimage.exception;

public class ImageTaskNotFoundException extends RuntimeException {
    public ImageTaskNotFoundException(String message) {
        super(message);
    }

    public ImageTaskNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}