package ru.evolenta.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.evolenta.task.config.SecurityConfiguration;
import ru.evolenta.task.controller.TaskController;
import ru.evolenta.task.dto.CreateTaskRequest;
import ru.evolenta.task.dto.UpdateTaskRequest;
import ru.evolenta.task.model.Status;
import ru.evolenta.task.model.Task;
import ru.evolenta.task.repository.TaskRepository;
import ru.evolenta.task.service.JwtService;
import ru.evolenta.task.service.TaskService;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@Import(SecurityConfiguration.class)
class TaskControllerTests {

    private final String jwtSecret = "TGpsK0tTaG5JWHcvZldrdGRtd3RjbXN6ZldsVVMzZDU";

    private final long jwtExpirationTime = 3600000L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private TaskRepository taskRepository;

    @Test
    @WithMockUser
    void testCreateTaskSuccess() throws Exception {
        CreateTaskRequest taskRequest = new CreateTaskRequest();
        taskRequest.setTitle("Новая задача");
        taskRequest.setDescription("Описание новой задачи");
        taskRequest.setCompletionDate(LocalDateTime.now().plusDays(1));

        Task task = new Task();
        BeanUtils.copyProperties(taskRequest, task);
        task.setId(1);
        task.setUserId(1L);
        task.setCreateDate(LocalDateTime.now());
        task.setStatus(new Status("Новая"));

        String authHeader = "Bearer " + generateToken(1L, "user", "ROLE_USER");
        Mockito.when(this.taskService
                .createTask(taskRequest, authHeader))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(task));

        mockMvc.perform(post("/task")
                        .with(csrf())
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(task.getId()))
                .andExpect(jsonPath("$.title").value(task.getTitle()))
                .andExpect(jsonPath("$.description").value(task.getDescription()));
    }

    @Test
    @WithMockUser
    void testCreateTaskInvalidCompletionDate() throws Exception {
        CreateTaskRequest taskRequest = new CreateTaskRequest();
        taskRequest.setTitle("Новая задача");
        taskRequest.setDescription("Описание новой задачи");
        taskRequest.setCompletionDate(LocalDateTime.now().minusDays(1));

        Task task = new Task();
        BeanUtils.copyProperties(taskRequest, task);
        task.setId(1);
        task.setUserId(1L);
        task.setCreateDate(LocalDateTime.now());
        task.setStatus(new Status("Новая"));

        String authHeader = "Bearer " + generateToken(1L, "user", "ROLE_USER");
        Mockito.when(this.taskService
                        .createTask(taskRequest, authHeader))
                .thenReturn(ResponseEntity.badRequest().build());

        mockMvc.perform(post("/task")
                        .with(csrf())
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    void testCreateTaskForbidden() throws Exception {
        CreateTaskRequest taskRequest = new CreateTaskRequest();
        taskRequest.setTitle("Новая задача");
        taskRequest.setDescription("Описание новой задачи");
        taskRequest.setCompletionDate(LocalDateTime.now().plusDays(1));

        mockMvc.perform(post("/task")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void testGetTasksSuccess() throws Exception {
        Mockito.when(this.taskRepository.findAllByOrderByCreateDateAsc()).thenReturn(getTasks());

        mockMvc.perform(get("/task"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value(getTasks().get(0).getTitle()))
                .andExpect(jsonPath("$[1].title").value(getTasks().get(1).getTitle()));
    }

    @Test
    @WithAnonymousUser
    void testGetTasksForbidden() throws Exception {
        mockMvc.perform(get("/task"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void testGetTaskSuccess() throws Exception {
        Task task = getTasks().getFirst();
        Mockito.when(this.taskService.getTask(1)).thenReturn(ResponseEntity.ok(task));

        int targetTaskId = 1;
        mockMvc.perform(get("/task/" + targetTaskId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(targetTaskId))
                .andExpect(jsonPath("$.userId").value(task.getUserId()))
                .andExpect(jsonPath("$.title").value(task.getTitle()))
                .andExpect(jsonPath("$.description").value(task.getDescription()))
                .andExpect(jsonPath("$.createDate").value(task.getCreateDate().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$.completionDate").value(task.getCompletionDate().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$.status").value(task.getStatus()));
    }

    @Test
    @WithMockUser
    void testGetTaskNotFound() throws Exception {
        int targetTaskId = 5;
        Mockito.when(this.taskService.getTask(targetTaskId)).thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/task/" + targetTaskId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testGetTaskForbidden() throws Exception {
        int targetTaskId = 1;
        mockMvc.perform(get("/task/" + targetTaskId))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void testGetTopicalTasksSuccess() throws Exception {
        Task task = getTasks().get(1);

        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(2);
        Mockito.when(this.taskService
                .getTopicalTasks(startDate, endDate))
                .thenReturn(List.of(getTasks().get(1)));

        mockMvc.perform(get("/task/topical")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value(task.getTitle()));
    }

    @Test
    @WithAnonymousUser
    void testGetTopicalTasksForbidden() throws Exception {
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(2);

        mockMvc.perform(get("/task/topical")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void testUpdateTaskSuccess() throws Exception {
        Task task = getTasks().getFirst();
        UpdateTaskRequest taskRequest = new UpdateTaskRequest();

        BeanUtils.copyProperties(task, taskRequest);
        taskRequest.setTitle("Новое название первой задачи");
        taskRequest.setDescription("Новое описание первой задачи");
        taskRequest.setCompletionDate(task.getCompletionDate().plusDays(4));
        taskRequest.setStatusId(2);
        BeanUtils.copyProperties(taskRequest, task, "statusId");
        task.setStatus(new Status("В работе"));

        int targetTaskId = 1;
        String authHeader = "Bearer " + generateToken(1L, "user", "ROLE_USER");
        Mockito.when(this.taskService
                        .updateTask(targetTaskId, taskRequest, authHeader))
                .thenReturn(ResponseEntity.ok(task));

        mockMvc.perform(put("/task/" + targetTaskId)
                        .with(csrf())
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(task.getId()))
                .andExpect(jsonPath("$.title").value(task.getTitle()))
                .andExpect(jsonPath("$.description").value(task.getDescription()))
                .andExpect(jsonPath("$.status").value(task.getStatus()));
    }

    @Test
    @WithMockUser
    void testUpdateTaskInvalidCompletionDate() throws Exception {
        UpdateTaskRequest taskRequest = new UpdateTaskRequest();
        taskRequest.setTitle("Новое название первой задачи");
        taskRequest.setDescription("Новое описание первой задачи");
        taskRequest.setCompletionDate(LocalDateTime.now().minusDays(4));
        taskRequest.setStatusId(2);

        int targetTaskId = 1;
        String authHeader = "Bearer " + generateToken(1L, "user", "ROLE_USER");
        Mockito.when(this.taskService
                        .updateTask(targetTaskId, taskRequest, authHeader))
                .thenReturn(ResponseEntity.badRequest().build());

        mockMvc.perform(put("/task/" + targetTaskId)
                        .with(csrf())
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testUpdateTaskNotFound() throws Exception {
        UpdateTaskRequest taskRequest = new UpdateTaskRequest();
        taskRequest.setTitle("Новое название первой задачи");
        taskRequest.setDescription("Новое описание первой задачи");
        taskRequest.setCompletionDate(LocalDateTime.now().plusDays(4));
        taskRequest.setStatusId(2);

        int targetTaskId = 5;
        String authHeader = "Bearer " + generateToken(1L, "user", "ROLE_USER");
        Mockito.when(this.taskService
                        .updateTask(targetTaskId, taskRequest, authHeader))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(put("/task/" + targetTaskId)
                        .with(csrf())
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testUpdateTaskForbidden() throws Exception {
        UpdateTaskRequest taskRequest = new UpdateTaskRequest();
        taskRequest.setTitle("Новое название первой задачи");
        taskRequest.setDescription("Новое описание первой задачи");
        taskRequest.setCompletionDate(LocalDateTime.now().plusDays(4));
        taskRequest.setStatusId(2);

        int targetTaskId = 1;

        mockMvc.perform(put("/task/" + targetTaskId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void testDeleteTaskSuccess() throws Exception {
        Task task = getTasks().get(1);

        int targetTaskId = 2;
        String authHeader = "Bearer " + generateToken(1L, "user", "ROLE_USER");
        Mockito.when(this.taskService
                        .deleteTask(targetTaskId, authHeader))
                .thenReturn(ResponseEntity.ok(task));

        mockMvc.perform(delete("/task/" + targetTaskId)
                        .with(csrf())
                        .header("Authorization", authHeader))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(targetTaskId))
                .andExpect(jsonPath("$.userId").value(task.getUserId()))
                .andExpect(jsonPath("$.title").value(task.getTitle()))
                .andExpect(jsonPath("$.description").value(task.getDescription()))
                .andExpect(jsonPath("$.createDate").value(task.getCreateDate().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$.completionDate").value(task.getCompletionDate().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$.status").value(task.getStatus()));
    }

    @Test
    @WithMockUser
    void testDeleteTaskNotFound() throws Exception {
        int targetTaskId = 5;
        String authHeader = "Bearer " + generateToken(1L, "user", "ROLE_USER");
        Mockito.when(this.taskService
                        .deleteTask(targetTaskId, authHeader))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(delete("/task/" + targetTaskId)
                        .with(csrf())
                        .header("Authorization", authHeader))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testDeleteTaskForbidden() throws Exception {
        int targetTaskId = 2;
        mockMvc.perform(delete("/task/" + targetTaskId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    private List<Task> getTasks() {
        Task firstTask = new Task(
                1,
                "Первая задача",
                "Описание первой задачи",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                1L,
                new Status("Новая")
        );
        Task secondTask = new Task(
                2,
                "Вторая задача",
                "Описание второй задачи",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                1L,
                new Status("Завершена")
        );
        return List.of(firstTask, secondTask);
    }

    private String generateToken(long userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userId);
        claims.put("username", username);
        claims.put("role", role);
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> extraClaims, String username) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
