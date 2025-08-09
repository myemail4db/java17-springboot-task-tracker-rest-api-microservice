package com.example.taskapi.dto;

import jakarta.validation.constraints.NotBlank;

public record TaskRequestDTO(
    @NotBlank(message = "title is mandatory") String title,
    String description
) {}
