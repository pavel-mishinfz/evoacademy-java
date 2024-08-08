package ru.evolenta.logger.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.evolenta.logger.model.Log;

import java.time.LocalDateTime;

@Repository
public interface LogRepository extends CrudRepository<Log, Long> {
    Iterable<Log> findAllByUsername(String username);
    Iterable<Log> findAllByTimestampIsGreaterThanEqualOrderByTimestampAsc(LocalDateTime start);
    Iterable<Log> findAllByTimestampIsLessThanEqualOrderByTimestampAsc(LocalDateTime end);
    Iterable<Log> findAllByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
