package ru.evolenta.task.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.evolenta.task.model.Status;
import ru.evolenta.task.model.Task;

import java.time.LocalDateTime;


@Repository
public interface TaskRepository extends CrudRepository<Task, Integer> {
    Iterable<Task> findAllByOrderByCreateDateAsc();
    Iterable<Task> findAllByStatusOrderByCompletionDateAsc(Status status);
    Iterable<Task> findAllByStatusAndCreateDateIsGreaterThanEqualOrderByCompletionDateAsc(
            Status status, LocalDateTime start
    );
    Iterable<Task> findAllByStatusAndCreateDateIsLessThanEqualOrderByCompletionDateAsc(
            Status status, LocalDateTime end
    );
    Iterable<Task> findAllByStatusAndCreateDateBetweenOrderByCompletionDateAsc(
            Status status, LocalDateTime start, LocalDateTime end
    );
}
