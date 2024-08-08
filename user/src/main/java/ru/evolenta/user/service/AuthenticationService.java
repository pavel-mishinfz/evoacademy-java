package ru.evolenta.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.evolenta.user.dto.JwtAuthenticationResponse;
import ru.evolenta.user.dto.LogRequest;
import ru.evolenta.user.dto.SignInRequest;
import ru.evolenta.user.dto.SignUpRequest;
import ru.evolenta.user.model.Action;
import ru.evolenta.user.model.Role;
import ru.evolenta.user.model.User;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RestTemplate restTemplate;

    /**
     * Регистрация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    public ResponseEntity<JwtAuthenticationResponse> signUp(SignUpRequest request) {

        User user = User.builder()
                .firstname(request.getFirstname())
                .surname(request.getSurname())
                .lastname(request.getLastname())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        if(userService.createUser(user).getStatusCode().isSameCodeAs(HttpStatus.BAD_REQUEST)) {
            return ResponseEntity.badRequest().build();
        }

        String jwt = jwtService.generateToken(user);
        createLog(Action.SIGN_UP);
        return ResponseEntity.status(HttpStatus.CREATED).body(new JwtAuthenticationResponse(jwt));
    }

    /**
     * Аутентификация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    public JwtAuthenticationResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));

        UserDetails user = userService
                .userDetailsService()
                .loadUserByUsername(request.getUsername());

        String jwt = jwtService.generateToken(user);
        createLog(Action.AUTH);
        return new JwtAuthenticationResponse(jwt);
    }

    private void createLog(Action action) {
        LogRequest logRequest = new LogRequest();
        logRequest.setUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        logRequest.setAction(action);

        restTemplate.postForEntity("http://localhost:8083/logger", logRequest, Void.class);
    }
}
