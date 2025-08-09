package com.example.taskapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
   🟢 This is the standard Spring Boot bootstrap class, annotated with @SpringBootApplication, which:
    - Enables component scanning
    - Enables auto-configuration
    - Starts the embedded server (when running java -jar)
 */

@SpringBootApplication
public class TaskApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaskApiApplication.class, args);
    }
}
