package com.knu.storyboard.user.exception;

public class UserEmailDuplicateException extends RuntimeException {
    public UserEmailDuplicateException(String message) {
        super(message);
    }
}
