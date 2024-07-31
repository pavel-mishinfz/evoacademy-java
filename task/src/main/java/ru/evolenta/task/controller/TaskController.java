package ru.evolenta.task.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.evolenta.task.dto.TaskDto;
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
    public ResponseEntity<Task> createTask(@RequestBody TaskDto taskDto) {
        return service.createTask(taskDto);
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
    public ResponseEntity<Task> updateTask(@PathVariable int id, @RequestBody TaskDto taskDto) {
        return service.updateTask(id, taskDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Task> deleteTask(@PathVariable int id) {
        return service.deleteTask(id);
    }
}
