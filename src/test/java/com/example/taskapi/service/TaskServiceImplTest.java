package com.example.taskapi.service;

import com.example.taskapi.dto.TaskRequestDTO;
import com.example.taskapi.dto.TaskResponseDTO;
import com.example.taskapi.entity.Task;
import com.example.taskapi.exception.TaskNotFoundException;
import com.example.taskapi.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskServiceImplTest {

    private TaskRepository taskRepository;
    private TaskServiceImpl taskService;

    private Task task;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRepository.class);
        taskService = new TaskServiceImpl(taskRepository);

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Title");
        task.setDescription("Test Description");
        task.setCompleted(false);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateTask() {
        TaskRequestDTO request = new TaskRequestDTO("Test Title", "Test Description");
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponseDTO response = taskService.createTask(request);

        assertEquals("Test Title", response.title());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testCreateTaskNext() {
        TaskRequestDTO request = new TaskRequestDTO("Title", "Desc");

        Task saved = new Task();
        saved.setId(1L);
        saved.setTitle("Title");
        saved.setDescription("Desc");

        when(taskRepository.save(any(Task.class))).thenReturn(saved);

        TaskResponseDTO result = taskService.createTask(request);

        assertEquals("Title", result.title());
    }

    @Test
    void testGetAllTasks() {
        when(taskRepository.findAll()).thenReturn(List.of(task));

        List<TaskResponseDTO> result = taskService.getAllTasks();

        assertEquals(1, result.size());
        assertEquals("Test Title", result.get(0).title());
    }

    @Test
    void testGetTaskById() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskResponseDTO result = taskService.getTaskById(1L);

        assertEquals("Test Title", result.title());
    }

    @Test
    void testGetTaskById_NotFound() {
        when(taskRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(2L));
    }

    @Test
    void testSearchByTitle() {
        when(taskRepository.findByTitleContainingIgnoreCase("test")).thenReturn(List.of(task));

        List<TaskResponseDTO> result = taskService.searchByTitle("test");

        assertEquals(1, result.size());
    }

    @Test
    void testGetByCompleted() {
        when(taskRepository.findByCompleted(false)).thenReturn(List.of(task));

        List<TaskResponseDTO> result = taskService.getByCompleted(false);

        assertEquals(1, result.size());
    }

    @Test
    void testGetByCreatedAfter() {
        LocalDateTime time = LocalDateTime.now().minusDays(1);
        when(taskRepository.findByCreatedAtAfter(time)).thenReturn(List.of(task));

        List<TaskResponseDTO> result = taskService.getByCreatedAfter(time);

        assertEquals(1, result.size());
    }

    @Test
    void testSearchByTitleAndCompleted() {
        when(taskRepository.findByTitleContainingIgnoreCaseAndCompleted("test", false))
                .thenReturn(List.of(task));

        List<TaskResponseDTO> result = taskService.searchByTitleAndCompleted("test", false);

        assertEquals(1, result.size());
    }

    @Test
    void testSearch() {
        when(taskRepository.searchTasks("test", false)).thenReturn(List.of(task));

        List<TaskResponseDTO> result = taskService.search("test", false);

        assertEquals(1, result.size());
    }
}
