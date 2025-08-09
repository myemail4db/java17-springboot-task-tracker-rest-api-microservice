package com.example.taskapi.exception;

public sealed interface AppException permits TaskNotFoundException, ValidationException {}
