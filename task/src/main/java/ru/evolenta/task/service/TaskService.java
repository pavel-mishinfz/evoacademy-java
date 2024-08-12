package ru.evolenta.task.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.evolenta.task.dto.CreateTaskRequest;
import ru.evolenta.task.dto.LogRequest;
import ru.evolenta.task.dto.UpdateTaskRequest;
import ru.evolenta.task.model.Action;
import ru.evolenta.task.model.Status;
import ru.evolenta.task.model.Task;
import ru.evolenta.task.repository.StatusRepository;
import ru.evolenta.task.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JwtService jwtService;

    public ResponseEntity<Task> createTask(CreateTaskRequest createTaskRequest, String authHeader) {
        String token = authHeader.substring(7);
        LocalDateTime dateTimeNow = LocalDateTime.now();
        LocalDateTime completionDate = createTaskRequest.getCompletionDate();
        if (dateTimeNow.isAfter(completionDate)) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Status> statusOptional = statusRepository.findByName("Новая");
        if (statusOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Task task = new Task(
                createTaskRequest.getTitle(),
                createTaskRequest.getDescription(),
                dateTimeNow,
                createTaskRequest.getCompletionDate(),
                jwtService.extractUserId(token),
                statusOptional.get()
        );
        task = taskRepository.save(task);
        createLog(Action.CREATE, task.getId(), token);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    public ResponseEntity<Task> getTask(int id) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        return taskOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    public Iterable<Task> getTopicalTasks(LocalDateTime startDate, LocalDateTime endDate) {
        Optional<Status> statusOptional = statusRepository.findByName("Завершена");
        if (statusOptional.isEmpty()) {
            return Collections.emptyList();
        }
        Status status = statusOptional.get();
        if(startDate == null && endDate == null) {
            return taskRepository.findAllByStatusNotOrderByCompletionDateAsc(status);
        }
        else if(startDate != null && endDate == null) {
            return taskRepository.findAllByStatusNotAndCreateDateIsGreaterThanEqualOrderByCompletionDateAsc(status, startDate);
        }
        else if(startDate == null && endDate != null) {
            return taskRepository.findAllByStatusNotAndCreateDateIsLessThanEqualOrderByCompletionDateAsc(status, endDate);
        }
        return taskRepository.findAllByStatusNotAndCreateDateBetweenOrderByCompletionDateAsc(status, startDate, endDate);
    }

    public ResponseEntity<Task> updateTask(int id, UpdateTaskRequest updateTaskRequest, String authHeader) {
        String token = authHeader.substring(7);
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if(!taskOptional.get().getUserId().equals(jwtService.extractUserId(token))) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Status> statusOptional = statusRepository.findById(updateTaskRequest.getStatusId());
        if (statusOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Status status = statusOptional.get();
        Task updatedTask = taskOptional.get();

        LocalDateTime createDate = updatedTask.getCreateDate();
        LocalDateTime newCompletionDate = updateTaskRequest.getCompletionDate();
        if (createDate.isAfter(newCompletionDate)) {
            return ResponseEntity.badRequest().build();
        }

        BeanUtils.copyProperties(updateTaskRequest, updatedTask, "statusId");
        updatedTask.setStatus(status);
        createLog(Action.UPDATE, updatedTask.getId(), token);
        return ResponseEntity.ok(taskRepository.save(updatedTask));
    }

    public ResponseEntity<Task> deleteTask(int id, String authHeader) {
        String token = authHeader.substring(7);
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isPresent()) {

            if(!taskOptional.get().getUserId().equals(jwtService.extractUserId(token))) {
                return ResponseEntity.badRequest().build();
            }

            Task task = taskOptional.get();
            taskRepository.deleteById(id);
            createLog(Action.DELETE, task.getId(), token);
            return ResponseEntity.ok().body(task);
        }
        return ResponseEntity.notFound().build();
    }

    private void createLog(Action action, Integer taskId, String token) {
        LogRequest logRequest = new LogRequest();
        logRequest.setUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        logRequest.setAction(action);
        logRequest.setTaskId(taskId);

        // Создание заголовков и добавление JWT токена
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<LogRequest> requestEntity = new HttpEntity<>(logRequest, headers);

        restTemplate.exchange(
                "http://logger-service/logger",
                HttpMethod.POST,
                requestEntity,
                Void.class
        );
    }
}
