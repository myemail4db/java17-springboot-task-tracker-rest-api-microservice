# Task Tracker – Java 17 REST API Microservice

![Build](https://img.shields.io/badge/build-passing-brightgreen)
![Coverage](https://img.shields.io/badge/coverage-100%25-brightgreen)
![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Java](https://img.shields.io/badge/Java-17-blue)

## Project Overview

This project is a full-featured, **enterprise-grade REST API microservice** built using Java 17 and Spring Boot 3. It simulates a task tracking system that demonstrates how to design, implement, test, document, and deploy a real-world backend application. It also incorporates features from the Java 17 Developer Certification to reinforce modern language capabilities.

---

## Purpose of the Project

To build a professional, maintainable RESTful backend microservice that follows full SDLC practices — including requirements gathering, high-level and detailed design documentation, development, testing, deployment, and CI/CD setup. The project is designed to:
- Serve as a **resume-quality artifact**
- Demonstrate **Java 17 certification features**
- Simulate real-world enterprise application design and structure

---

## Purpose of the Application

This RESTful Task Tracker application allows users (or client applications) to:
- Create new tasks
- Retrieve existing tasks
- Filter tasks by title, date, and status

It models a clean, modular architecture that enables:
- Data validation
- Custom error handling
- Complete test coverage
- API documentation
- Logging and environment separation

---

## Project Planning

### Functional Scope
- Full CRUD functionality for tasks
- Filter/search by title, completion, date
- DTO validation using `@Valid`
- Global exception handling
- REST API endpoints documented with OpenAPI (Swagger UI)

### Non-Functional Scope
- 100% test coverage (unit + integration via JUnit 5, Mockito, MockMvc)
- SLF4J logging across all layers (traceable flow)
- Logback output to console and file
- Separate dev and prod environment configs
- Executable JAR build with Maven
- Incorporates Java 17 features:
  - `record` for DTOs
  - `sealed` exceptions
  - Pattern matching
  - Enhanced `switch` statements
- CI/CD pipeline with GitHub Actions (build/test)

---

## Architecture & Documentation

- High-Level Design (HLD): System context, layered architecture, request/response flow  
- Detailed Design (DD): Component breakdown, DTOs, service interactions, class responsibilities  
- Logging strategy: trace-level observability across components  
- API contracts and Swagger UI endpoint documentation  

> Design documentation can be found in the [`/docs/designs`] directory.
> - [HighLevelDesign.md](./docs/designs/HighLevelDesign.md)
> - [DetailedLevelDesign.md](docs/designs/DetailLevelDesign.md)

> Breakdown of Code documentation can be found in the ['docs/breakdownOfCode'] directory.
> - [TaskController.java - Line-by-line Breakdown.md](./docs/breakdownOfCode/TaskController.java%20-%20Line-by-Line%20Breakdown.md)

---

## Features

- Java 17 language features: `record`, `sealed`, `switch`, pattern matching
- Spring Boot 3 REST API
- DTO validation using Jakarta Bean Validation
- Custom exception handling
- OpenAPI 3.0 documentation with Swagger UI
- Console and file logging (SLF4J + Logback)
- 100% unit and integration test coverage
- MySQL (dev) and H2 (test) database profiles
- Maven packaging (`java -jar`)
- Dev and prod environment configs

---

## Technologies

| Layer         | Technology                        |
|--------------|------------------------------------|
| Language      | Java 17                           |
| Framework     | Spring Boot 3                     |
| REST API      | Spring Web                        |
| Data Access   | Spring Data JPA                   |
| DB            | MySQL (Dev), H2 (Test)            |
| Validation    | Jakarta Bean Validation (`@Valid`)|
| Logging       | SLF4J + Logback                   |
| Docs          | springdoc-openapi-ui (Swagger)    |
| Testing       | JUnit 5, Mockito, MockMvc, JaCoCo |
| CI/CD         | GitHub Actions (placeholder)      |
| Build Tool    | Maven                             |

---

## Project Structure

```
docs/
├── HighLelvelDesign.md
└── DetailLevelDesign.md
src/
├── main/
│ ├── java/com/example/taskapi/
│ │ ├── controller/
│ │ ├── service/
│ │ ├── repository/
│ │ ├── dto/
│ │ ├── entity/
│ │ └── exception/
│ └── resources/
│ ├── application-dev.properties
│ ├── application-prod.properties
│ └── logback.xml
├── test/
│ ├── unit/
│ └── integration/
```

---

## Running the Application

### Prerequisites
- Java 17
- Maven
- MySQL (if using `dev` profile)

### Build & Run
```bash
mvn clean package
java -jar target/task-api.jar --spring.profiles.active=dev
```

---

## API Documentation

Open after starting the app:
```bash
http://localhost:8080/swagger-ui.html
```

---

## Testing & Coverage

```bash
# Run tests
mvn clean test

# View JaCoCo report
open target/site/jacoco/index.html
```

## 🔌 Sample Endpoints

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | /api/tasks | Create a task |
| GET | /api/tasks | Get all tasks |
| GET | /api/tasks/{id} | Get task by ID |
| GET | /api/tasks/filter/title | Filter by title |
| GET | /api/tasks/filter/completed| Filter by completion |
| GET | /api/tasks/filter/created-after | Filter by creation date |
| GET | /api/tasks/search| Flexible search |

---

## GitHub Actions CI (Placeholder)

Basic build/test workflow runs on every push to main. See .github/workflows/build.yml.

---

## License

This project is licensed under the MIT License.

---

## Contributing

Open to improvements or enhancements. Feel free to fork, open PRs, or file issues.
