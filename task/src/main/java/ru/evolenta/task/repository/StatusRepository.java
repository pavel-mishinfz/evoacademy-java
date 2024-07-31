package ru.evolenta.task.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.evolenta.task.model.Status;

import java.util.Optional;

@Repository
public interface StatusRepository extends CrudRepository<Status, Integer> {
    Optional<Status> findByName(String name);
}
