package ru.evolenta.logger.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.evolenta.logger.dto.LogRequest;
import ru.evolenta.logger.model.Log;
import ru.evolenta.logger.repository.LogRepository;
import ru.evolenta.logger.service.LogService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/logger")
public class LogController {

    @Autowired
    private LogRepository repository;

    @Autowired
    private LogService service;

    @PostMapping
    public ResponseEntity<Void> createLog(@RequestBody LogRequest logRequest) {
        return service.createLog(logRequest);
    }

    @GetMapping(params = "username")
    public Iterable<Log> getLogsByUsername(@RequestParam String username) {
        return repository.findAllByUsername(username);
    }

    @GetMapping
    public Iterable<Log> getLogs(
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate
    ) {
        return service.getLogs(startDate, endDate);
    }
}
