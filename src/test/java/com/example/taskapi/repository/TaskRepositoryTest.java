package com.example.taskapi.repository;

import com.example.taskapi.entity.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();

        Task task1 = new Task();
        task1.setTitle("Write unit tests");
        task1.setDescription("For the repository layer");
        task1.setCompleted(false);
        task1.setCreatedAt(LocalDateTime.now().minusDays(1));
        task1.setUpdatedAt(LocalDateTime.now());
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setTitle("Write integration tests");
        task2.setDescription("For controller layer");
        task2.setCompleted(true);
        task2.setCreatedAt(LocalDateTime.now().minusDays(2));
        task2.setUpdatedAt(LocalDateTime.now());
        taskRepository.save(task2);
    }

    @Test
    void testFindByTitleContainingIgnoreCase() {
        List<Task> result = taskRepository.findByTitleContainingIgnoreCase("unit");
        assertEquals(1, result.size());
        assertTrue(result.get(0).getTitle().contains("unit"));
    }

    @Test
    void testFindByCompletedTrue() {
        List<Task> result = taskRepository.findByCompletedTrue();
        assertEquals(1, result.size());
        assertTrue(result.get(0).isCompleted());
    }

    @Test
    void testFindByCreatedAtAfter() {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1).minusHours(1);
        List<Task> result = taskRepository.findByCreatedAtAfter(oneDayAgo);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getCreatedAt().isAfter(oneDayAgo));
    }

    @Test
    void testFindByTitleOrDescriptionContainingIgnoreCase() {
        List<Task> result = taskRepository.findByTitleOrDescriptionContainingIgnoreCase("controller", "controller");
        assertEquals(1, result.size());
        assertTrue(result.get(0).getDescription().contains("controller"));
    }
}
