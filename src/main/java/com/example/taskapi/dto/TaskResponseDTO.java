package com.example.taskapi.dto;

import java.time.LocalDateTime;

/**
 * DTO for sending Task information in response payloads.
 */
public record TaskResponseDTO(
    Long id,
    String title,
    String description,
    boolean completed,
    LocalDateTime createdAt
) {}

