package com.example.taskapi.repository;

import com.example.taskapi.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByTitleContainingIgnoreCase(String keyword);

    List<Task> findByCompleted(boolean completed);

    List<Task> findByCreatedAtAfter(LocalDateTime timestamp);

    List<Task> findByTitleContainingIgnoreCaseAndCompleted(String keyword, boolean completed);

    List<Task> findByTitleOrDescriptionContainingIgnoreCase(String title, String description);

    List<Task> findByCompletedTrue();

    @Query("SELECT t FROM Task t WHERE " +
           "(:keyword IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:completed IS NULL OR t.completed = :completed)")
    List<Task> searchTasks(@Param("keyword") String keyword,
                           @Param("completed") Boolean completed);
}
