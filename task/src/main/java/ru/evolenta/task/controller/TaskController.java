package ru.evolenta.task.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.evolenta.task.dto.CreateTaskRequest;
import ru.evolenta.task.dto.UpdateTaskRequest;
import ru.evolenta.task.model.Task;
import ru.evolenta.task.repository.TaskRepository;
import ru.evolenta.task.service.TaskService;


@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskRepository repository;

    @Autowired
    private TaskService service;

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody CreateTaskRequest createTaskRequest) {
        return service.createTask(createTaskRequest);
    }

    @GetMapping
    public Iterable<Task> getTasks() {
        return repository.findAllByOrderByCreateDateAsc();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable int id) {
        return service.getTask(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable int id, @RequestBody UpdateTaskRequest updateTaskRequest) {
        return service.updateTask(id, updateTaskRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Task> deleteTask(@PathVariable int id) {
        return service.deleteTask(id);
    }
}
