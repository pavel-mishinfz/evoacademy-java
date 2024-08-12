package ru.evolenta.task;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.evolenta.task.model.Status;

import java.security.Key;
import java.util.*;

        import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class StatusControllerTests {

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpirationTime;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String userToken;
    private String adminToken;
    private final String STATUS_PREFIX = "/status";
    private String baseUrl;


    @BeforeEach
    void setUp() {
        userToken = generateToken(1L, "user", "ROLE_USER");
        adminToken = generateToken(1L, "admin", "ROLE_ADMIN");
        baseUrl = "http://localhost:" + port;

    }

    @Test
    void testCreateStatus() {
        Status status = new Status("NEW");
        HttpEntity<Status> request = new HttpEntity<>(status, createHeaders(adminToken));
        ResponseEntity<Status> response = restTemplate.exchange(
                baseUrl + STATUS_PREFIX, HttpMethod.POST, request, Status.class
        );

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getId());

        request = new HttpEntity<>(status, createHeaders(userToken));
        response = restTemplate.exchange(
                baseUrl + STATUS_PREFIX, HttpMethod.POST, request, Status.class
        );

        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    void testGetStatuses() {
        initDatabase();

        HttpEntity<String> request = new HttpEntity<>(createHeaders(adminToken));
        ResponseEntity<List> response = restTemplate.exchange(
                baseUrl + STATUS_PREFIX, HttpMethod.GET, request, List.class
        );

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size());

        request = new HttpEntity<>(createHeaders(userToken));
        response = restTemplate.exchange(
                baseUrl + STATUS_PREFIX, HttpMethod.GET, request, List.class
        );

        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    void testGetStatus() {
        initDatabase();

        int targetStatusId = 2;
        HttpEntity<String> request = new HttpEntity<>(createHeaders(adminToken));
        ResponseEntity<Status> response = restTemplate.exchange(
                baseUrl + STATUS_PREFIX + "/" + targetStatusId, HttpMethod.GET, request, Status.class
        );

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(targetStatusId, response.getBody().getId());

        request = new HttpEntity<>(createHeaders(userToken));
        response = restTemplate.exchange(
                baseUrl + STATUS_PREFIX + "/" + targetStatusId, HttpMethod.GET, request, Status.class
        );

        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    void testUpdateStatus() {
        initDatabase();

        int targetStatusId = 2;
        Status newStatus = new Status("Обратная связь");
        HttpEntity<Status> request = new HttpEntity<>(newStatus, createHeaders(adminToken));
        ResponseEntity<Status> response = restTemplate.exchange(
                baseUrl + STATUS_PREFIX + "/" + targetStatusId, HttpMethod.PUT, request, Status.class
        );

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(newStatus.getName(), response.getBody().getName());

        request = new HttpEntity<>(createHeaders(userToken));
        response = restTemplate.exchange(
                baseUrl + STATUS_PREFIX + "/" + targetStatusId, HttpMethod.PUT, request, Status.class
        );

        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    void testDeleteStatus() {
        initDatabase();

        int targetStatusId = 2;
        HttpEntity<String> request = new HttpEntity<>(createHeaders(adminToken));
        ResponseEntity<Status> response = restTemplate.exchange(
                baseUrl + STATUS_PREFIX + "/" + targetStatusId, HttpMethod.DELETE, request, Status.class
        );

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(targetStatusId, response.getBody().getId());

        request = new HttpEntity<>(createHeaders(userToken));
        response = restTemplate.exchange(
                baseUrl + STATUS_PREFIX + "/" + targetStatusId, HttpMethod.DELETE, request, Status.class
        );

        assertEquals(403, response.getStatusCode().value());
    }

    private void initDatabase() {
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
