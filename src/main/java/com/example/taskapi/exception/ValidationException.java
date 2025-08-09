package com.example.taskapi.exception;

/**
 * Thrown when input validation fails.
 * Implements AppException for switch-style handling.
 */
public final class ValidationException extends RuntimeException implements AppException {

    public ValidationException(String message) {
        super(message);
    }
}
