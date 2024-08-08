package ru.evolenta.task.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private RestTemplate restTemplate;

    public ResponseEntity<Task> createTask(CreateTaskRequest createTaskRequest) {
        LocalDateTime dateTimeNow = LocalDateTime.now();
        LocalDateTime completionDate = createTaskRequest.getCompletionDate();
        if (dateTimeNow.isAfter(completionDate)) {
            return ResponseEntity.badRequest().build();
        }

        Status status = statusRepository.findByName("Новая").get();
        Task task = new Task(
                createTaskRequest.getTitle(),
                createTaskRequest.getDescription(),
                dateTimeNow,
                createTaskRequest.getCompletionDate(),
                status
        );
        task = taskRepository.save(task);
        createLog(Action.CREATE, task.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    public ResponseEntity<Task> getTask(int id) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        return taskOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    public Iterable<Task> getTopicalTasks(LocalDateTime startDate, LocalDateTime endDate) {
        Status status = statusRepository.findByName("Завершена").get();
        if(startDate == null && endDate == null) {
            return taskRepository.findAllByStatusOrderByCompletionDateAsc(status);
        }
        else if(startDate != null && endDate == null) {
            return taskRepository.findAllByStatusAndCreateDateIsGreaterThanEqualOrderByCompletionDateAsc(status, startDate);
        }
        else if(startDate == null && endDate != null) {
            return taskRepository.findAllByStatusAndCreateDateIsLessThanEqualOrderByCompletionDateAsc(status, endDate);
        }
        return taskRepository.findAllByStatusAndCreateDateBetweenOrderByCompletionDateAsc(status, startDate, endDate);
    }

    public ResponseEntity<Task> updateTask(int id, UpdateTaskRequest updateTaskRequest) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
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
        createLog(Action.UPDATE, updatedTask.getId());
        return ResponseEntity.ok(taskRepository.save(updatedTask));
    }

    public ResponseEntity<Task> deleteTask(int id) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            taskRepository.deleteById(id);
            createLog(Action.DELETE, task.getId());
            return ResponseEntity.ok().body(task);
        }
        return ResponseEntity.notFound().build();
    }

    private void createLog(Action action, Integer taskId) {
        LogRequest logRequest = new LogRequest();
        logRequest.setUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        logRequest.setAction(action);
        logRequest.setTaskId(taskId);

        restTemplate.postForEntity("http://localhost:8083/logger", logRequest, Void.class);
    }
}
