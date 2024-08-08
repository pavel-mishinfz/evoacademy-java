package ru.evolenta.logger.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.evolenta.logger.dto.LogRequest;
import ru.evolenta.logger.model.Log;
import ru.evolenta.logger.repository.LogRepository;

import java.time.LocalDateTime;

@Service
public class LogService {

    @Autowired
    private LogRepository repository;

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

}
