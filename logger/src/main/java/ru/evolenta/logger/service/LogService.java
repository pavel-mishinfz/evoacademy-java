package ru.evolenta.logger.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.evolenta.logger.dto.LogRequest;
import ru.evolenta.logger.dto.TaskResponse;
import ru.evolenta.logger.dto.UserResponse;
import ru.evolenta.logger.model.Log;
import ru.evolenta.logger.repository.LogRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class LogService {

    @Autowired
    private LogRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    public ResponseEntity<Void> createLog(LogRequest logRequest) {
        String usernameCurrentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!logRequest.getUsername().equals(usernameCurrentUser)) {
            return ResponseEntity.badRequest().build();
        }
        Log log = new Log();
        BeanUtils.copyProperties(logRequest, log);
        log.setTimestamp(LocalDateTime.now());
        repository.save(log);
        return ResponseEntity.ok().build();
    }

    public Iterable<Log> getLogs(LocalDateTime startDate, LocalDateTime endDate) {
        if(startDate == null && endDate == null) {
            return repository.findAll();
        }
        else if(startDate != null && endDate == null) {
            return repository.findAllByTimestampIsGreaterThanEqualOrderByTimestampAsc(startDate);
        }
        else if(startDate == null && endDate != null) {
            return repository.findAllByTimestampIsLessThanEqualOrderByTimestampAsc(endDate);
        }
        return repository.findAllByTimestampBetween(startDate, endDate);
    }

    public ResponseEntity<Log> updateLog(long id, LogRequest logRequest, String authHeader) {
        String token = authHeader.substring(7);
        Optional<Log> logOptional = repository.findById(id);
        if(logOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        String username = logRequest.getUsername();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            restTemplate.exchange(
                    "http://user-service/user?username=" + username,
                    HttpMethod.GET,
                    entity,
                    UserResponse.class
            );
        } catch (HttpClientErrorException.NotFound e) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Integer taskId = logRequest.getTaskId();
            restTemplate.exchange(
                    "http://task-service/task/" + taskId,
                    HttpMethod.GET,
                    entity,
                    TaskResponse.class
            );
        } catch (HttpClientErrorException.NotFound e) {
            return ResponseEntity.badRequest().build();
        }


        Log log = logOptional.get();
        BeanUtils.copyProperties(logRequest, log);
        return ResponseEntity.ok(repository.save(log));
    }

    public ResponseEntity<Log> deleteLog(long id) {
        Optional<Log> logOptional = repository.findById(id);
        if(logOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Log log = logOptional.get();
        repository.deleteById(id);
        return ResponseEntity.ok(log);
    }

}
