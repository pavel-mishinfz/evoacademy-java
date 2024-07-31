package ru.evolenta.task.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.evolenta.task.dto.StatusDto;
import ru.evolenta.task.model.Status;
import ru.evolenta.task.repository.StatusRepository;
import ru.evolenta.task.service.StatusService;

@RestController
@RequestMapping("/status")
public class StatusController {

    @Autowired
    private StatusRepository repository;

    @Autowired
    private StatusService service;

    @PostMapping
    public ResponseEntity<Status> createStatus(@RequestBody StatusDto statusDto) {
        return service.createStatus(statusDto);
    }

    @GetMapping
    public Iterable<Status> getStatuses() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Status> getStatus(@PathVariable int id) {
        return service.getStatus(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Status> updateStatus(@PathVariable int id, @RequestBody StatusDto statusDto) {
        return service.updateStatus(id, statusDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Status> deleteStatus(@PathVariable int id) {
        return service.deleteStatus(id);
    }
}
