package ru.evolenta.task.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.evolenta.task.dto.CreateTaskRequest;
import ru.evolenta.task.dto.UpdateTaskRequest;
import ru.evolenta.task.model.Task;
import ru.evolenta.task.repository.TaskRepository;
import ru.evolenta.task.service.TaskService;

import java.time.LocalDateTime;


@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskRepository repository;

    @Autowired
    private TaskService service;

    @PostMapping
    public ResponseEntity<Task> createTask(
            @RequestBody CreateTaskRequest createTaskRequest,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        return service.createTask(createTaskRequest, authHeader);
    }

    @GetMapping
    public Iterable<Task> getTasks() {
        return repository.findAllByOrderByCreateDateAsc();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable int id) {
        return service.getTask(id);
    }

    @GetMapping("/topical")
    public Iterable<Task> getTopicalTasks(
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate
    ) {
        return service.getTopicalTasks(startDate, endDate);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @PathVariable int id,
            @RequestBody UpdateTaskRequest updateTaskRequest,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        return service.updateTask(id, updateTaskRequest, authHeader);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Task> deleteTask(
            @PathVariable int id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        return service.deleteTask(id, authHeader);
    }
}
