package com.example.taskapi.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        logger.error("Validation failed for request: {}", ex.getBindingResult(), ex);
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .collect(Collectors.joining(", "));
        
        ErrorResponse error = new ErrorResponse("Task creation/update validation error", message);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskNotFound(TaskNotFoundException ex) {
        logger.error("Task not found exception occurred", ex);
        ErrorResponse error = new ErrorResponse("Task not found", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // (Optional) generic handler for other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        logger.error("Unhandled exception in REST API", ex);
        ErrorResponse error = new ErrorResponse("Server error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleAppExceptions(RuntimeException ex) {
        logger.error("Unhandled exception in REST API", ex);
        if (ex instanceof AppException appEx) {
            if (appEx instanceof ValidationException ve) {
                logger.warn("Validation error: {}", ve.getMessage(), ve);
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse(
                        "Validation Error: At least one of 'keyword' or 'completed' must be provided", ve.getMessage()));
            }
        }
        logger.error("Unhandled RuntimeException in REST API", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("Unexpected error occurred", ex.getMessage()));
    }
    
}
