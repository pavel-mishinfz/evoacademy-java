package ru.evolenta.task;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.evolenta.task.dto.CreateTaskRequest;
import ru.evolenta.task.model.Status;
import ru.evolenta.task.model.Task;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TaskControllerTests {

	@Value("${security.jwt.secret}")
	private String jwtSecret;

	@Value("${security.jwt.expiration-time}")
	private long jwtExpirationTime;

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	private WireMockServer wireMockServer;

	private String userToken;
	private String adminToken;
	private final String STATUS_PREFIX = "/status";
	private final String TASK_PREFIX = "/task";
	private String baseUrl;
	

	@BeforeEach
	void setUp() {
		userToken = generateToken(1L, "user", "ROLE_USER");
		adminToken = generateToken(1L, "admin", "ROLE_ADMIN");
		baseUrl = "http://localhost:" + port;

		wireMockServer = new WireMockServer(8003);
		wireMockServer.start();
//		WireMock.configureFor("localhost", 8003);

		// Настройка мока для WireMock
		wireMockServer.stubFor(post(urlEqualTo("/logger"))
				.willReturn(aResponse()
						.withStatus(HttpStatus.OK.value())));
	}

	@AfterEach
	public void tearDown() {
		wireMockServer.stop();
	}

	@Test
	void testCreateTask() {
		createStatuses();
		CreateTaskRequest task = new CreateTaskRequest();
		task.setTitle("Первая задача");
		task.setDescription("Описание первой задачи");
		task.setCompletionDate(LocalDateTime.now().plusDays(1));

		HttpEntity<CreateTaskRequest> request = new HttpEntity<>(task, createHeaders(adminToken));
		ResponseEntity<Task> response = restTemplate.exchange(
				baseUrl + TASK_PREFIX, HttpMethod.POST, request, Task.class
		);

		assertEquals(200, response.getStatusCode().value());
		assertNotNull(response.getBody());
		assertEquals(1, response.getBody().getId());

		wireMockServer.verify(postRequestedFor(urlEqualTo("/logger")));
	}

	private void createStatuses() {
		List<Status> statuses = new ArrayList<>();
		statuses.add(new Status("Новая"));
		statuses.add(new Status("В работе"));
		statuses.add(new Status("Завершена"));
		for (Status status : statuses) {
			HttpEntity<Status> request = new HttpEntity<>(status, createHeaders(adminToken));
			restTemplate.exchange(
					baseUrl + STATUS_PREFIX, HttpMethod.POST, request, Status.class
			);
		}
	}

	private HttpHeaders createHeaders(String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);
		return headers;
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
