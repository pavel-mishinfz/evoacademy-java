package ru.evolenta.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.evolenta.user.model.User;
import ru.evolenta.user.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    public User createUser(User user) {
        if (repository.existsByUsername(user.getUsername())) {
            return null;
        }
        return repository.save(user);
    }

    public User getByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }

    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

}
