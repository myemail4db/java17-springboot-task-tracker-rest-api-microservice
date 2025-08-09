package com.example.taskapi.service;

import com.example.taskapi.dto.TaskRequestDTO;
import com.example.taskapi.dto.TaskResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskService {

    TaskResponseDTO createTask(TaskRequestDTO request);

    List<TaskResponseDTO> getAllTasks();

    TaskResponseDTO getTaskById(Long id);

    // Custom queries
    List<TaskResponseDTO> searchByTitle(String keyword);

    List<TaskResponseDTO> getByCompleted(boolean completed);

    List<TaskResponseDTO> getByCreatedAfter(LocalDateTime timestamp);

    List<TaskResponseDTO> searchByTitleAndCompleted(String keyword, boolean completed);

    List<TaskResponseDTO> search(String keyword, Boolean completed);
}
