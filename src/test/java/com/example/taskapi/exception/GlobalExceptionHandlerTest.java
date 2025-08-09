package com.example.taskapi.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setup() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleValidation() throws NoSuchMethodException {
        // Simulate a field error
        FieldError fieldError = new FieldError("task", "title", "Title is required");

        // Create binding result and add error
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "task");
        bindingResult.addError(fieldError);

        // Get a MethodParameter from a dummy method
        Method method = DummyController.class.getMethod("dummyMethod", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        // Create the exception
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        // Call the handler
        ResponseEntity<ErrorResponse> response = handler.handleValidation(ex);

        // Assert the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Task creation/update validation error", response.getBody().getError());
        assertEquals("Title is required", response.getBody().getMessage());
    }

    // Dummy controller to create a MethodParameter
    static class DummyController {
        public void dummyMethod(@Autowired String input) {}
    }

    @Test
    void testHandleTaskNotFound() {
        TaskNotFoundException ex = new TaskNotFoundException("ID 5 not found");

        ResponseEntity<ErrorResponse> response = handler.handleTaskNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Task not found", response.getBody().getError());
        assertEquals("ID 5 not found", response.getBody().getMessage());
    }

    @Test
    void testHandleGeneric() {
        Exception ex = new Exception("Something broke");

        ResponseEntity<ErrorResponse> response = handler.handleGeneric(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Server error", response.getBody().getError());
        assertEquals("Something broke", response.getBody().getMessage());
    }

    @Test
    void testHandleAppExceptions_WithValidationException() {
        ValidationException ex = new ValidationException("Missing title");

        ResponseEntity<ErrorResponse> response = handler.handleAppExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation Error: At least one of 'keyword' or 'completed' must be provided", response.getBody().getError());
        assertEquals("Missing title", response.getBody().getMessage());
    }

    @Test
    void testHandleAppExceptions_WithOtherRuntimeException() {
        RuntimeException ex = new RuntimeException("Unknown error");

        ResponseEntity<ErrorResponse> response = handler.handleAppExceptions(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Unexpected error occurred", response.getBody().getError());
    }

    @Test
    void testHandleAppExceptions_WithValidationExceptionAndBadRequest() {
        // Arrange
        ValidationException ex = new ValidationException("Missing title");

        // Act
        ResponseEntity<ErrorResponse> response = handler.handleAppExceptions(ex);

        // Log the response body
        System.out.println("Response body: " + response.getBody().getError() + " - " + response.getBody().getMessage());

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(
            "Validation Error: At least one of 'keyword' or 'completed' must be provided", 
            response.getBody().getError());
        assertEquals("Missing title", response.getBody().getMessage());
    }    

    // ValidationException branch (TRUE → TRUE)
    @Test
    void handleAppExceptions_Returns400_ForValidationException() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        com.example.taskapi.exception.ValidationException ex =
                new com.example.taskapi.exception.ValidationException("Missing title");

        ResponseEntity<ErrorResponse> resp = handler.handleAppExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertEquals(
            "Validation Error: At least one of 'keyword' or 'completed' must be provided", 
            resp.getBody().getError());
        assertEquals("Missing title", resp.getBody().getMessage());
    }    

    // ValidationException branch (TRUE → FALSE)
    @Test
    void handleAppExceptions_Returns500_ForOtherAppException() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        TaskNotFoundException ex = new TaskNotFoundException("ID 42 not found");

        ResponseEntity<ErrorResponse> resp = handler.handleAppExceptions(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        assertEquals("Unexpected error occurred", resp.getBody().getError());
        assertEquals("ID 42 not found", resp.getBody().getMessage());
    }
}
