package com.example.taskapi.integration;

import com.example.taskapi.dto.TaskRequestDTO;
import com.example.taskapi.entity.Task;
import com.example.taskapi.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test") // <--- ADD THIS
class TaskIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        taskRepository.deleteAll(); // Clean up before each test
        Task task = new Task();
        task.setTitle("Integration Task");
        task.setDescription("Integration test description");
        task.setCompleted(false);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        taskRepository.save(task);
    }

    @Test
    void testGetAllTasks() throws Exception {
        mockMvc.perform(get("/api/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Integration Task"));
    }

    @Test
    void testCreateTask() throws Exception {
        TaskRequestDTO newTask = new TaskRequestDTO("New Task", "From integration test");

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTask)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("New Task"));
    }

    @Test
    void testGetTaskById() throws Exception {
        Task task = taskRepository.findAll().get(0);

        mockMvc.perform(get("/api/tasks/" + task.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Integration Task"));
    }

    @Test
    void testSearchByTitle() throws Exception {
        mockMvc.perform(get("/api/tasks/filter/title")
                .param("keyword", "integration"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Integration Task"));
    }

    @Test
    void testFilterByCompleted() throws Exception {
        mockMvc.perform(get("/api/tasks/filter/completed")
                .param("status", "false"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].completed").value(false));
    }

    @Test
    void testFilterByCreatedAfter() throws Exception {
        String yesterday = LocalDateTime.now().minusDays(1).toString();

        mockMvc.perform(get("/api/tasks/filter/created-after")
                .param("date", yesterday))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Integration Task"));
    }

    @Test
    void testSearchByTitleAndCompleted() throws Exception {
        mockMvc.perform(get("/api/tasks/filter/title-and-completed")
                .param("keyword", "integration")
                .param("completed", "false"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Integration Task"));
    }

    @Test
    void testFlexibleSearch() throws Exception {
        mockMvc.perform(get("/api/tasks/search")
                .param("keyword", "integration")
                .param("completed", "false"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Integration Task"));
    }

    @Test
    void testInvalidIdReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/tasks/99999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value(containsString("Task not found")));
    }

    @Test
    void testValidationErrorOnCreate() throws Exception {
        TaskRequestDTO invalid = new TaskRequestDTO("", "Missing title");

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", containsString("Task creation/update validation error")));
    }

}

