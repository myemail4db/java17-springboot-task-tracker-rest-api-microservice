<!-- omit in toc -->
# TaskController.java — Line-by-Line Breakdown

- [Breakdown of TaskController.java](#breakdown-of-taskcontrollerjava)
  - [Package \& imports](#package--imports)
  - [Class declaration \& base mapping](#class-declaration--base-mapping)
  - [Create (POST)](#create-post)
  - [Get all (GET)](#get-all-get)
  - [Get by id (GET)](#get-by-id-get)
  - [Filter: created after a date (GET)](#filter-created-after-a-date-get)
  - [Filter: title + completed (GET)](#filter-title--completed-get)
  - [Flexible search with guard (GET)](#flexible-search-with-guard-get)
  - [Filter: title only (GET) \& completed only (GET)](#filter-title-only-get--completed-only-get)
- [How the flow works (at runtime)](#how-the-flow-works-at-runtime)
- [Copy-paste sample requests](#copy-paste-sample-requests)
  - [Create](#create)
  - [Get all](#get-all)
  - [Get by id](#get-by-id)
  - [Created after](#created-after)
  - [Title + completed](#title--completed)
  - [Flexible search](#flexible-search)
  - [Title only](#title-only)
  - [Completed only](#completed-only)

---

Here’s a developer-friendly breakdown of TaskController.java. 
Afterwards, there is a quick flow overview and copy-pasteable sample requests.

# Breakdown of TaskController.java

## Package & imports

```java
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
```

- Organizes the class in the $controller$ layer.

- Pulls in DTOs (input/output models), custom exception, and the $TaskService$ dependency.

- Brings validation (@Valid$, $@NotBlank$), date parsing ($@DateTimeFormat$), HTTP types, and Spring MVC annotations.

- Uses $LocalDateTime$ for temporal filtering and $List$ for collections.

---

## Class declaration & base mapping

```java
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
```

- **@RestController**: marks this as a REST endpoint provider (methods return JSON by default).

- **@RequestMapping("/api/tasks")**: every method path is rooted at /api/tasks.

- Constructor injection of **TaskService** keeps the controller thin and testable.

---

## Create (POST)

```java
@PostMapping
public ResponseEntity<TaskResponseDTO> create(@Valid @RequestBody TaskRequestDTO request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(request));
}
```

- POST /api/tasks

- @RequestBody binds JSON → TaskRequestDTO.

- @Valid triggers Bean Validation on the DTO (e.g., @NotBlank fields).

- Returns 201 Created with the saved task (TaskResponseDTO).

---

## Get all (GET)

```java
@GetMapping
public List<TaskResponseDTO> getAll() {
    return taskService.getAllTasks();
}
```

- GET /api/tasks

- Straight delegation to the service; Spring serializes the List<TaskResponseDTO> to JSON with 200 OK.

---

## Get by id (GET)

```java
@GetMapping("/{id}")
public ResponseEntity<TaskResponseDTO> getById(@PathVariable Long id) {
    return ResponseEntity.ok(taskService.getTaskById(id));
}
```

- GET /api/tasks/{id}

- @PathVariable extracts the id from the URL.

- If not found, the service throws TaskNotFoundException → mapped to 404 by your GlobalExceptionHandler.

---

## Filter: created after a date (GET)

```java
@GetMapping("/filter/created-after")
public ResponseEntity<List<TaskResponseDTO>> getByCreatedAfter(
    @RequestParam("date") 
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
    return ResponseEntity.ok(taskService.getByCreatedAfter(date));
}
```

- GET /api/tasks/filter/created-after?date=2025-08-10T12:30:00

- @DateTimeFormat parses ISO-8601 into LocalDateTime.

- Useful for time-based queries/audits.

---

## Filter: title + completed (GET)

```java
@GetMapping("/filter/title-and-completed")
public ResponseEntity<List<TaskResponseDTO>> searchByTitleAndCompleted(
        @RequestParam(name = "keyword") @NotBlank String keyword,
        @RequestParam(name = "completed") boolean completed) {

    List<TaskResponseDTO> results = taskService.searchByTitleAndCompleted(keyword, completed);
    return ResponseEntity.ok(results);
}
```

- GET /api/tasks/filter/title-and-completed?keyword=foo&completed=true

- @NotBlank enforces a non-empty search keyword at the edge of the system.

- Combines text filtering with completion status.

---

## Flexible search with guard (GET)

```java
@GetMapping("/search")
public ResponseEntity<List<TaskResponseDTO>> searchFlexible(
        @RequestParam(name = "keyword", required = false) String keyword,
        @RequestParam(name = "completed", required = false) Boolean completed) {

    if ((keyword == null || keyword.trim().isEmpty()) && completed == null) {
        throw new ValidationException("At least one of 'keyword' or 'completed' must be provided.");
    }

    List<TaskResponseDTO> results = taskService.search(keyword, completed);
    return ResponseEntity.ok(results);
}
```

- GET /api/tasks/search?...

- Both params are optional, but at least one must be supplied; otherwise a ValidationException → 400 response via the global handler.

- Delegates the flexible branching to the service.

---

## Filter: title only (GET) & completed only (GET)

```java
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
```

- GET /api/tasks/filter/title?keyword=foo and GET /api/tasks/filter/completed?status=true

- Simple, focused filters for common UI use cases.

---

# How the flow works (at runtime)

    Request hits controller → Spring binds path/query/body to method parameters.

    Validation runs (@Valid, @NotBlank, plus the manual guard in /search).

    Controller delegates to TaskService for business logic & database access.

    Response is built (ResponseEntity + DTOs).

    Exceptions (e.g., not found, validation) are converted to uniform JSON by GlobalExceptionHandler.

---

# Copy-paste sample requests

---

## Create

```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"Write docs","description":"Break down controller"}'
```

---

## Get all

```bash
curl http://localhost:8080/api/tasks
```

---

## Get by id

```bash
curl http://localhost:8080/api/tasks/1
```

---

## Created after

```bash
curl "http://localhost:8080/api/tasks/filter/created-after?date=2025-08-10T12:30:00"
```

---

## Title + completed

```bash
curl "http://localhost:8080/api/tasks/filter/title-and-completed?keyword=docs&completed=true"
```

---

## Flexible search

```bash
# keyword only
curl "http://localhost:8080/api/tasks/search?keyword=docs"

# completed only
curl "http://localhost:8080/api/tasks/search?completed=true"

# neither (should return 400)
curl "http://localhost:8080/api/tasks/search"
```

---

## Title only

```bash
curl "http://localhost:8080/api/tasks/filter/title?keyword=docs"
```

---

## Completed only

```bash
curl "http://localhost:8080/api/tasks/filter/completed?status=false"
```