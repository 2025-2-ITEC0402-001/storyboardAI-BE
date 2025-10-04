package com.knu.storyboard.common.exception;

import com.knu.storyboard.auth.exception.*;
import com.knu.storyboard.common.file.FileStorageException;
import com.knu.storyboard.user.exception.UserEmailDuplicateException;
import com.knu.storyboard.user.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OAuthBadRequestException.class)
    public ResponseEntity<ProblemDetail> handleOAuthBadRequestExceptionException(OAuthBadRequestException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("OAuth Bad Request");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(OAuthErrorException.class)
    public ResponseEntity<ProblemDetail> handleOAuthErrorExceptionException(OAuthErrorException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        problemDetail.setTitle("OAuth Error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    @ExceptionHandler(OAuthNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleOAuthNotFoundExceptionException(OAuthNotFoundException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problemDetail.setTitle("OAuth Not Found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(TokenBadRequestException.class)
    public ResponseEntity<ProblemDetail> handleTokenBadRequestExceptionException(TokenBadRequestException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Token Bad Request");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(TokenConflictException.class)
    public ResponseEntity<ProblemDetail> handleTokenConflictExceptionException(TokenConflictException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
        problemDetail.setTitle("Token Conflict");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ProblemDetail> handleTokenExpiredException(TokenExpiredException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(461), e.getMessage());
        problemDetail.setTitle("Token Expired");
        return ResponseEntity.status(HttpStatusCode.valueOf(461)).body(problemDetail);
    }

    @ExceptionHandler(TokenStolenException.class)
    public ResponseEntity<ProblemDetail> handleTokenStolenException(TokenStolenException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(462), e.getMessage());
        problemDetail.setTitle("Token Stolen");
        return ResponseEntity.status(HttpStatusCode.valueOf(462)).body(problemDetail);
    }

    @ExceptionHandler(UserEmailDuplicateException.class)
    public ResponseEntity<ProblemDetail> handleUserEmailDuplicateException(UserEmailDuplicateException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("User Email Duplicate");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleUserNotFoundException(UserNotFoundException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problemDetail.setTitle("User NotFound");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ProblemDetail> handleFileStorageException(FileStorageException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        problemDetail.setTitle("File Storage Exception");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }
}
