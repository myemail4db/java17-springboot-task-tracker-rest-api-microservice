package com.example.taskapi.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void allArgsConstructor_setsFields() {
        ErrorResponse er = new ErrorResponse("Validation error", "Missing title");
        assertEquals("Validation error", er.getError());
        assertEquals("Missing title", er.getMessage());
    }

    @Test
    void noArgsConstructor_andSetters_work() {
        ErrorResponse er = new ErrorResponse();
        assertNull(er.getError());
        assertNull(er.getMessage());

        er.setError("Server error");
        er.setMessage("Boom");

        assertEquals("Server error", er.getError());
        assertEquals("Boom", er.getMessage());
    }

    @Test
    void setters_acceptNulls() {
        ErrorResponse er = new ErrorResponse("x", "y");
        er.setError(null);
        er.setMessage(null);
        assertNull(er.getError());
        assertNull(er.getMessage());
    }
}
