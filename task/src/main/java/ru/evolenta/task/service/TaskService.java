package ru.evolenta.task.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.evolenta.task.dto.TaskDto;
import ru.evolenta.task.model.Status;
import ru.evolenta.task.model.Task;
import ru.evolenta.task.repository.StatusRepository;
import ru.evolenta.task.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private StatusRepository statusRepository;

    public ResponseEntity<Task> createTask(TaskDto taskDto) {
        Optional<Status> statusOptional = statusRepository.findById(taskDto.getStatusId());
        if (statusOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        LocalDateTime dateTimeNow = LocalDateTime.now();
        LocalDateTime completionDate = taskDto.getCompletionDate();
        if (dateTimeNow.isAfter(completionDate)) {
            return ResponseEntity.badRequest().build();
        }

        Status status = statusOptional.get();
        Task task = new Task(
                taskDto.getTitle(),
                taskDto.getDescription(),
                dateTimeNow,
                taskDto.getCompletionDate(),
                status
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(taskRepository.save(task));
    }

    public ResponseEntity<Task> getTask(int id) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        return taskOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    public ResponseEntity<Task> updateTask(int id, TaskDto taskDto) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<Status> statusOptional = statusRepository.findById(taskDto.getStatusId());
        if (statusOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Status status = statusOptional.get();
        Task updatedTask = taskOptional.get();

        LocalDateTime createDate = updatedTask.getCreateDate();
        LocalDateTime newCompletionDate = taskDto.getCompletionDate();
        if (createDate.isAfter(newCompletionDate)) {
            return ResponseEntity.badRequest().build();
        }

        BeanUtils.copyProperties(taskDto, updatedTask, "statusId");
        updatedTask.setStatus(status);
        return ResponseEntity.ok(taskRepository.save(updatedTask));
    }

    public ResponseEntity<Task> deleteTask(int id) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            taskRepository.deleteById(id);
            return ResponseEntity.ok().body(task);
        }
        return ResponseEntity.notFound().build();
    }
}
