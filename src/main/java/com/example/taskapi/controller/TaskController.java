package com.example.taskapi.controller;

import com.example.taskapi.dto.TaskRequestDTO;
import com.example.taskapi.dto.TaskResponseDTO;
import com.example.taskapi.exception.ValidationException;
import com.example.taskapi.service.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponseDTO> create(@Valid @RequestBody TaskRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(request));
    }

    @GetMapping
    public List<TaskResponseDTO> getAll() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @GetMapping("/filter/created-after")
    public ResponseEntity<List<TaskResponseDTO>> getByCreatedAfter(
        @RequestParam("date") 
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        return ResponseEntity.ok(taskService.getByCreatedAfter(date));
    }

    @GetMapping("/filter/title-and-completed")
    public ResponseEntity<List<TaskResponseDTO>> searchByTitleAndCompleted(
            @RequestParam(name = "keyword") @NotBlank String keyword,
            @RequestParam(name = "completed") boolean completed) {

        List<TaskResponseDTO> results = taskService.searchByTitleAndCompleted(keyword, completed);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TaskResponseDTO>> searchFlexible(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "completed", required = false) Boolean completed) {

        // Optional: Add validation if both are null
        if ((keyword == null || keyword.trim().isEmpty()) && completed == null) {
            throw new ValidationException("At least one of 'keyword' or 'completed' must be provided.");
        }

        List<TaskResponseDTO> results = taskService.search(keyword, completed);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/filter/title")
    public ResponseEntity<List<TaskResponseDTO>> searchByTitle(@RequestParam("keyword") String keyword) {
        List<TaskResponseDTO> results = taskService.searchByTitle(keyword);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/filter/completed")
    public ResponseEntity<List<TaskResponseDTO>> getByCompleted(@RequestParam("status") boolean completed) {
        List<TaskResponseDTO> results = taskService.getByCompleted(completed);
        return ResponseEntity.ok(results);
    }

}
