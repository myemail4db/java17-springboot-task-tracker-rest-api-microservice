package com.example.taskapi.exception;

/**
 * Thrown when a task with the specified ID does not exist.
 * Implements sealed AppException interface.
 */
public final class TaskNotFoundException extends RuntimeException implements AppException {

    public TaskNotFoundException(String message) {
        super(message);
    }
}
