package ru.evolenta.task.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.evolenta.task.dto.StatusRequest;
import ru.evolenta.task.model.Status;
import ru.evolenta.task.repository.StatusRepository;

import java.util.Optional;

@Service
public class StatusService {

    @Autowired
    private StatusRepository repository;

    public ResponseEntity<Status> createStatus(StatusRequest statusRequest) {
        Optional<Status> statusOptional = repository.findByName(statusRequest.getName());
        if (statusOptional.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        Status status = new Status(statusRequest.getName());
        return ResponseEntity.ok(repository.save(status));
    }

    public ResponseEntity<Status> getStatus(int id) {
        Optional<Status> statusOptional = repository.findById(id);
        return statusOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    public ResponseEntity<Status> updateStatus(int id, StatusRequest statusRequest) {
        Optional<Status> statusOptional = repository.findById(id);
        if (statusOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Status updatedStatus = statusOptional.get();

        String statusName = updatedStatus.getName();
        String newStatusName = statusRequest.getName();
        if (statusName.equals(newStatusName)) {
            return ResponseEntity.badRequest().build();
        }

        updatedStatus.setName(statusRequest.getName());
        return ResponseEntity.ok(repository.save(updatedStatus));
    }

    public ResponseEntity<Status> deleteStatus(int id) {
        Optional<Status> statusOptional = repository.findById(id);
        if (statusOptional.isPresent()) {
            Status status = statusOptional.get();
            repository.deleteById(id);
            return ResponseEntity.ok().body(status);
        }
        return ResponseEntity.notFound().build();
    }
}
