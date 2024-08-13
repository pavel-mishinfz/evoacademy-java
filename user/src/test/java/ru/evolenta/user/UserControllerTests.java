package ru.evolenta.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.evolenta.user.config.SecurityConfiguration;
import ru.evolenta.user.controller.UserController;
import ru.evolenta.user.model.Role;
import ru.evolenta.user.model.User;
import ru.evolenta.user.repository.UserRepository;
import ru.evolenta.user.service.JwtService;
import ru.evolenta.user.service.UserService;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfiguration.class)
class UserControllerTests {

	private final String jwtSecret = "TGpsK0tTaG5JWHcvZldrdGRtd3RjbXN6ZldsVVMzZDU";

	private final long jwtExpirationTime = 3600000L;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private UserService userService;

	@MockBean
	private JwtService jwtService;

	@MockBean
	private UserRepository userRepository;

	@Test
	@WithMockUser
	void testGetUserMeSuccess() throws Exception {
		User user = new User(
				1L,
				"Firstname",
				"Surname",
				"Lastname",
				"username",
				"password",
				Role.ROLE_USER
		);

		String authHeader = "Bearer " + generateToken(1L, "user", "ROLE_USER");
		Mockito.when(this.userService
						.getCurrentUser())
				.thenReturn(user);

		mockMvc.perform(get("/user/me"))
				.andDo(print())
				.andExpect(status().isCreated());
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
