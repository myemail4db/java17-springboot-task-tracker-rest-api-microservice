# Low-Level Design (LLD)

## Entity Design
```java
@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    private String description;

    private boolean completed = false;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters, setters, constructors
}
```

## DTO Structure
```java
public class TaskRequestDTO {
    @NotBlank
    private String title;
    private String description;
}

public class TaskResponseDTO {
    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;
}
```

## Service Layer
```java
public interface TaskService {
    TaskResponseDTO createTask(TaskRequestDTO request);
    List<TaskResponseDTO> getAllTasks();
    TaskResponseDTO getTaskById(Long id);
}

@Service
public class TaskServiceImpl implements TaskService {
    // Inject repository and mapper
    // Convert DTOs, persist, return responses
}
```

## Controller Layer
```java
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @PostMapping
    public ResponseEntity<TaskResponseDTO> create(@Valid @RequestBody TaskRequestDTO request);

    @GetMapping
    public List<TaskResponseDTO> getAll();

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getById(@PathVariable Long id);
}
```

## Exception Handling
```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(...);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(...);
}
```

## Testing Strategy
- Unit tests for controller, service, repository
- Mocked services using Mockito
- In-memory H2 DB for repository tests
- Code coverage tracked with JaCoCo

## Replace DTOs with Java 17 record:
```java
public record TaskRequestDTO(
    @NotBlank String title,
    String description
) {}

public record TaskResponseDTO(
    Long id,
    String title,
    String description,
    boolean completed,
    LocalDateTime createdAt
) {}
```

## Use sealed exceptions with enhanced switch:
```java
public sealed interface AppException permits TaskNotFoundException, ValidationException {}

public final class TaskNotFoundException extends RuntimeException implements AppException {
    public TaskNotFoundException(String message) { super(message); }
}

public final class ValidationException extends RuntimeException implements AppException {
    public ValidationException(String message) { super(message); }
}

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<Map<String, String>> handleAppExceptions(AppException ex) {
        return switch (ex) {
            case TaskNotFoundException tnfe -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", tnfe.getMessage()));
            case ValidationException ve -> ResponseEntity.badRequest()
                .body(Map.of("error", ve.getMessage()));
        };
    }
}
```

## DTO Structure (Java 17 Record)
```java
public record TaskRequestDTO(
    @NotBlank String title,
    String description
) {}

public record TaskResponseDTO(
    Long id,
    String title,
    String description,
    boolean completed,
    LocalDateTime createdAt
) {}
```

---

## Exception Handling (Java 17 Sealed Classes)
```java
public sealed interface AppException permits TaskNotFoundException, ValidationException {}

public final class TaskNotFoundException extends RuntimeException implements AppException {
    public TaskNotFoundException(String message) {
        super(message);
    }
}

public final class ValidationException extends RuntimeException implements AppException {
    public ValidationException(String message) {
        super(message);
    }
}

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<Map<String, String>> handleAppExceptions(AppException ex) {
        return switch (ex) {
            case TaskNotFoundException tnfe -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", tnfe.getMessage()));
            case ValidationException ve -> ResponseEntity.badRequest()
                .body(Map.of("error", ve.getMessage()));
        };
    }
}
```
