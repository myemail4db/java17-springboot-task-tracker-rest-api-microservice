package com.example.taskapi.service;

import com.example.taskapi.dto.TaskRequestDTO;
import com.example.taskapi.dto.TaskResponseDTO;
import com.example.taskapi.entity.Task;
import com.example.taskapi.exception.TaskNotFoundException;
import com.example.taskapi.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    private TaskResponseDTO toDTO(Task task) {
        return new TaskResponseDTO(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.isCompleted(),
            task.getCreatedAt()
        );
    }

    @Override
    public TaskResponseDTO createTask(TaskRequestDTO request) {
        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());

        return toDTO(taskRepository.save(task));
    }

    @Override
    public List<TaskResponseDTO> getAllTasks() {
        return taskRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    public TaskResponseDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException("Task not found: " + id));
        return toDTO(task);
    }

    // Custom query implementations
    @Override
    public List<TaskResponseDTO> searchByTitle(String keyword) {
        return taskRepository.findByTitleContainingIgnoreCase(keyword).stream().map(this::toDTO).toList();
    }

    @Override
    public List<TaskResponseDTO> getByCompleted(boolean completed) {
        return taskRepository.findByCompleted(completed).stream().map(this::toDTO).toList();
    }

    @Override
    public List<TaskResponseDTO> getByCreatedAfter(LocalDateTime timestamp) {
        return taskRepository.findByCreatedAtAfter(timestamp).stream().map(this::toDTO).toList();
    }

    @Override
    public List<TaskResponseDTO> searchByTitleAndCompleted(String keyword, boolean completed) {
        return taskRepository.findByTitleContainingIgnoreCaseAndCompleted(keyword, completed).stream().map(this::toDTO).toList();
    }

    @Override
    public List<TaskResponseDTO> search(String keyword, Boolean completed) {
        return taskRepository.searchTasks(keyword, completed).stream().map(this::toDTO).toList();
    }
}
