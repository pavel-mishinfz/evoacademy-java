package ru.evolenta.task.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.evolenta.task.model.Task;


@Repository
public interface TaskRepository extends CrudRepository<Task, Integer> {
    Iterable<Task> findAllByOrderByCreateDateAsc();
}
