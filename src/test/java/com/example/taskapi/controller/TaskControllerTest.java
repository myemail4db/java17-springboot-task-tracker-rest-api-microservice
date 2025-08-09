package com.example.taskapi.controller;

import com.example.taskapi.dto.TaskRequestDTO;
import com.example.taskapi.dto.TaskResponseDTO;
import com.example.taskapi.exception.TaskNotFoundException;
import com.example.taskapi.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(TaskController.class)
@ExtendWith(SpringExtension.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private TaskResponseDTO sampleResponse;

    @BeforeEach
    void setUp() {
        sampleResponse = new TaskResponseDTO(
            1L,
            "Test Task",
            "Sample Description",
            false,
            LocalDateTime.now()
        );
    }

    @Test
    void testCreateTask() throws Exception {
        TaskRequestDTO request = new TaskRequestDTO("Test Task", "Sample Description");

        Mockito.when(taskService.createTask(any())).thenReturn(sampleResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title", is("Test Task")));
    }

    @Test
    void testCreateTask_ValidationError() throws Exception {
        TaskRequestDTO invalidRequest = new TaskRequestDTO("", "desc");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", containsString("Task creation/update validation error")));
    }


    @Test
    void testGetAllTasks() throws Exception {
        Mockito.when(taskService.getAllTasks()).thenReturn(List.of(sampleResponse));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].title", is("Test Task")));
    }

    @Test
    void testGetTaskById_Found() throws Exception {
        Mockito.when(taskService.getTaskById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void testGetTaskById_NotFound() throws Exception {
        Mockito.when(taskService.getTaskById(99L)).thenThrow(new TaskNotFoundException("Task not found: 99"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error", containsString("Task not found")));
    }

    @Test
    void testSearchByTitle() throws Exception {
        Mockito.when(taskService.searchByTitle("test")).thenReturn(List.of(sampleResponse));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks/filter/title")
                .param("keyword", "test"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title", is("Test Task")));
    }

    @Test
    void testGetByCompleted() throws Exception {
        Mockito.when(taskService.getByCompleted(false)).thenReturn(List.of(sampleResponse));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks/filter/completed")
                .param("status", "false"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].completed", is(false)));
    }

    @Test
    void testGetByCreatedAfter() throws Exception {
        Mockito.when(taskService.getByCreatedAfter(any())).thenReturn(List.of(sampleResponse));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks/filter/created-after")
                .param("date", LocalDateTime.now().minusDays(1).toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    void testSearchByTitleAndCompleted() throws Exception {
        Mockito.when(taskService.searchByTitleAndCompleted("test", false))
            .thenReturn(List.of(sampleResponse));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks/filter/title-and-completed")
                .param("keyword", "test")
                .param("completed", "false"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title", is("Test Task")));
    }

    // Case: Both parameters provided → valid path
    @Test
    void testSearchFlexible() throws Exception {
        Mockito.when(taskService.search("test", false))
            .thenReturn(List.of(sampleResponse));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks/search")
                .param("keyword", "test")
                .param("completed", "false"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title", is("Test Task")));
    }

    // Covers the else path and exercises the service call with completed == null.
    @Test
    void testSearchFlexible_WithKeywordOnly() throws Exception {
        Mockito.when(taskService.search(eq("test"), isNull()))
            .thenReturn(List.of(new TaskResponseDTO(1L, "test", "desc", false, LocalDateTime.now())));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks/search")
                .param("keyword", "test"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title", is("test")));
    }

    // Covers the path when keyword is empty but completed is provided.
    @Test
    void testSearchFlexible_WithCompletedOnly() throws Exception {
        Mockito.when(taskService.search(isNull(), eq(true)))
            .thenReturn(List.of(new TaskResponseDTO(1L, "done", "desc", true, LocalDateTime.now())));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks/search")
                .param("completed", "true"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].completed", is(true)));
    }

    // This is a test for the search endpoint where keyword is null and completed is null
    @Test
    void testSearchFlexibleThrowsValidationExceptionWhenBothParamsMissing() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks/search"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", containsString("At least one of 'keyword' or 'completed' must be provided")));
    }

    // This is a test for the search endpoint where keyword is empty and completed is null
    @Test
    void testSearchFlexibleThrowsValidationExceptionWhenKeywordIsEmptyAndCompletedIsNull() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks/search")
                .param("keyword", "")
                .param("completed", (String) null))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", containsString("At least one of 'keyword' or 'completed' must be provided")));
    }

    // This is a test for the search endpoint where keyword is null and completed is null
    @Test
    void testSearchFlexibleThrowsValidationExceptionWhenKeywordIsNullAndCompletedIsNull() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks/search")
                .param("keyword", (String) null)
                .param("completed", (String) null))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", containsString("Validation Error: At least one of 'keyword' or 'completed' must be provided")));
    }
}
