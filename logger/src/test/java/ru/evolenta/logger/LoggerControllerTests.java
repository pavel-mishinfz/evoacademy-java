package ru.evolenta.logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.evolenta.logger.config.SecurityConfiguration;
import ru.evolenta.logger.controller.LogController;
import ru.evolenta.logger.dto.LogRequest;
import ru.evolenta.logger.model.Action;
import ru.evolenta.logger.model.Log;
import ru.evolenta.logger.repository.LogRepository;
import ru.evolenta.logger.service.JwtService;
import ru.evolenta.logger.service.LogService;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LogController.class)
@Import(SecurityConfiguration.class)
class LoggerControllerTests {

	private final String jwtSecret = "TGpsK0tTaG5JWHcvZldrdGRtd3RjbXN6ZldsVVMzZDU";

	private final long jwtExpirationTime = 3600000L;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private LogService logService;

	@MockBean
	private JwtService jwtService;

	@MockBean
	private LogRepository logRepository;

	@Test
	@WithMockUser
	void testCreateLogSuccess() throws Exception {
		LogRequest logRequest = new LogRequest();
		logRequest.setUsername("user");
		logRequest.setAction(Action.CREATE);
		logRequest.setTaskId(1);

		String authHeader = "Bearer " + generateToken(1L, "user", "ROLE_USER");
		Mockito.when(this.logService
						.createLog(logRequest))
				.thenReturn(ResponseEntity.ok().build());

		mockMvc.perform(post("/logger")
						.with(csrf())
						.header("Authorization", authHeader)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(logRequest)))
				.andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser
	void testCreateLogBadRequest() throws Exception {
		LogRequest logRequest = new LogRequest();
		logRequest.setUsername("anotherUser");
		logRequest.setAction(Action.CREATE);
		logRequest.setTaskId(1);

		String authHeader = "Bearer " + generateToken(1L, "user", "ROLE_USER");
		Mockito.when(this.logService
						.createLog(logRequest))
				.thenReturn(ResponseEntity.badRequest().build());

		mockMvc.perform(post("/logger")
						.with(csrf())
						.header("Authorization", authHeader)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(logRequest)))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	@WithAnonymousUser
	void testCreateLogForbidden() throws Exception {
		LogRequest logRequest = new LogRequest();
		logRequest.setUsername("user");
		logRequest.setAction(Action.CREATE);
		logRequest.setTaskId(1);

		mockMvc.perform(post("/logger")
						.with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(logRequest)))
				.andDo(print())
				.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(roles = {"ADMIN"})
	void testGetLogsByUsernameSuccess() throws Exception {
		Log log1 = new Log();
		log1.setId(1L);
		log1.setUsername("user");
		log1.setAction(Action.AUTH);
		log1.setTimestamp(LocalDateTime.now());

		Log log2 = new Log();
		log2.setId(2L);
		log2.setUsername("user");
		log2.setAction(Action.UPDATE);
		log2.setTaskId(1);
		log2.setTimestamp(LocalDateTime.now());

		List<Log> logs = List.of(log1, log2);

		String targetUsername = "user";
		Mockito.when(this.logRepository.findAllByUsername(targetUsername))
				.thenReturn(logs);

		mockMvc.perform(get("/logger")
						.queryParam("username", targetUsername))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(logs.size()))
				.andExpect(jsonPath("$[0].id").value(log1.getId()))
				.andExpect(jsonPath("$[0].username").value(log1.getUsername()))
				.andExpect(jsonPath("$[1].id").value(log2.getId()))
				.andExpect(jsonPath("$[1].username").value(log2.getUsername()));
	}

	@Test
	@WithMockUser
	void testGetLogsByUsernameForbidden() throws Exception {
		String targetUsername = "user";
		mockMvc.perform(get("/logger")
						.queryParam("username", targetUsername))
				.andDo(print())
				.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(roles = {"ADMIN"})
	void testGetLogsSuccess() throws Exception {
		LocalDateTime startDate = LocalDateTime.now().minusDays(1);
		LocalDateTime endDate = LocalDateTime.now().plusDays(1);

		Log log1 = new Log();
		log1.setId(1L);
		log1.setUsername("user");
		log1.setAction(Action.AUTH);
		log1.setTimestamp(LocalDateTime.now());

		Log log2 = new Log();
		log2.setId(2L);
		log2.setUsername("user");
		log2.setAction(Action.UPDATE);
		log2.setTaskId(1);
		log2.setTimestamp(LocalDateTime.now().plusDays(2));

		List<Log> logs = List.of(log1, log2);

		Log targetLog = logs.getFirst();
		Mockito.when(this.logService.getLogs(startDate, endDate))
				.thenReturn(List.of(targetLog));

		mockMvc.perform(get("/logger")
						.queryParam("startDate", startDate.toString())
						.queryParam("endDate", endDate.toString()))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].id").value(targetLog.getId()))
				.andExpect(jsonPath("$[0].username").value(targetLog.getUsername()))
				.andExpect(jsonPath("$[0].action").value(targetLog.getAction().toString()))
				.andExpect(jsonPath("$[0].taskId").value(targetLog.getTaskId()))
				.andExpect(jsonPath("$[0].timestamp").value(targetLog.getTimestamp().format(DateTimeFormatter.ISO_DATE_TIME)));
	}

	@Test
	@WithMockUser
	void testGetLogsForbidden() throws Exception {
		LocalDateTime startDate = LocalDateTime.now().minusDays(1);
		LocalDateTime endDate = LocalDateTime.now().plusDays(1);

		mockMvc.perform(get("/logger")
						.queryParam("startDate", startDate.toString())
						.queryParam("endDate", endDate.toString()))
				.andDo(print())
				.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(roles = {"ADMIN"})
	void testUpdateLogSuccess() throws Exception {
		Log log = new Log();
		log.setId(1L);
		log.setUsername("user");
		log.setAction(Action.UPDATE);
		log.setId(1);
		log.setTimestamp(LocalDateTime.now());

		LogRequest logRequest = new LogRequest();
		logRequest.setUsername("NEW USERNAME");
		logRequest.setAction(Action.CREATE);
		logRequest.setTaskId(2);

		BeanUtils.copyProperties(logRequest, log);

		long targetLogId = log.getId();
		String authHeader = "Bearer " + generateToken(1L, "user", "ROLE_ADMIN");
		Mockito.when(this.logService.updateLog(targetLogId, logRequest, authHeader))
				.thenReturn(ResponseEntity.ok(log));

		mockMvc.perform(put("/logger/" + targetLogId)
						.with(csrf())
						.header("Authorization", authHeader)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(logRequest)))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(log.getId()))
				.andExpect(jsonPath("$.username").value(log.getUsername()))
				.andExpect(jsonPath("$.action").value(log.getAction().toString()))
				.andExpect(jsonPath("$.taskId").value(log.getTaskId()))
				.andExpect(jsonPath("$.timestamp").value(log.getTimestamp().format(DateTimeFormatter.ISO_DATE_TIME)));
	}

	@Test
	@WithMockUser(roles = {"ADMIN"})
	void testUpdateLogNotFound() throws Exception {
		LogRequest logRequest = new LogRequest();
		logRequest.setUsername("NEW USERNAME");
		logRequest.setAction(Action.CREATE);
		logRequest.setTaskId(2);

		long targetLogId = 5L;
		String authHeader = "Bearer " + generateToken(1L, "user", "ROLE_ADMIN");
		Mockito.when(this.logService.updateLog(targetLogId, logRequest, authHeader))
				.thenReturn(ResponseEntity.notFound().build());

		mockMvc.perform(put("/logger/" + targetLogId)
						.with(csrf())
						.header("Authorization", authHeader)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(logRequest)))
				.andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser
	void testUpdateLogForbidden() throws Exception {
		LogRequest logRequest = new LogRequest();
		logRequest.setUsername("NEW USERNAME");
		logRequest.setAction(Action.CREATE);
		logRequest.setTaskId(2);

		long targetLogId = 5L;
		mockMvc.perform(put("/logger/" + targetLogId)
						.with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(logRequest)))
				.andDo(print())
				.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(roles = {"ADMIN"})
	void testDeleteLogSuccess() throws Exception {
		Log log = new Log();
		log.setId(1L);
		log.setUsername("user");
		log.setAction(Action.UPDATE);
		log.setId(1);
		log.setTimestamp(LocalDateTime.now());

		long targetLogId = log.getId();
		Mockito.when(this.logService.deleteLog(targetLogId))
				.thenReturn(ResponseEntity.ok(log));

		mockMvc.perform(delete("/logger/" + targetLogId)
						.with(csrf()))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(log.getId()))
				.andExpect(jsonPath("$.username").value(log.getUsername()))
				.andExpect(jsonPath("$.action").value(log.getAction().toString()))
				.andExpect(jsonPath("$.taskId").value(log.getTaskId()))
				.andExpect(jsonPath("$.timestamp").value(log.getTimestamp().format(DateTimeFormatter.ISO_DATE_TIME)));
	}

	@Test
	@WithMockUser(roles = {"ADMIN"})
	void testDeleteLogNotFound() throws Exception {
		long targetLogId = 5L;
		Mockito.when(this.logService.deleteLog(targetLogId))
				.thenReturn(ResponseEntity.notFound().build());

		mockMvc.perform(delete("/logger/" + targetLogId)
						.with(csrf()))
				.andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser
	void testDeleteLogForbidden() throws Exception {
		long targetLogId = 5L;
		mockMvc.perform(delete("/logger/" + targetLogId)
						.with(csrf()))
				.andDo(print())
				.andExpect(status().isForbidden());
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
