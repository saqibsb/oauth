package com.app.oauth.service;

import com.app.oauth.model.User;
import com.app.oauth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private TestUserRepository repository;
    private BCryptPasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        repository = new TestUserRepository();
        UserRepository userRepository = repository.asRepository();
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void registerUser_success() {
        User result = userService.registerUser("john", "john@example.com", "password123");

        assertNotNull(result);
        assertEquals("john", result.getUsername());
        assertEquals("john@example.com", result.getEmail());
        assertTrue(passwordEncoder.matches("password123", result.getPassword()));
    }

    @Test
    void findByUsername_found() {
        User user = new User();
        user.setUsername("john");

        repository.save(user);

        Optional<User> result = userService.findByUsername("john");

        assertTrue(result.isPresent());
        assertEquals("john", result.get().getUsername());
    }

    @Test
    void validatePassword_true() {
        String hashedPassword = passwordEncoder.encode("password123");

        boolean valid = userService.validatePassword("password123", hashedPassword);

        assertTrue(valid);
    }

    private static class TestUserRepository {

        private final Map<String, User> usersByUsername = new HashMap<>();
        private final Map<String, User> usersByEmail = new HashMap<>();
        private long nextId = 1L;

        UserRepository asRepository() {
            return (UserRepository) Proxy.newProxyInstance(
                    UserRepository.class.getClassLoader(),
                    new Class<?>[]{UserRepository.class},
                    (proxy, method, args) -> switch (method.getName()) {
                        case "findByUsername" -> Optional.ofNullable(usersByUsername.get((String) args[0]));
                        case "findByEmail" -> Optional.ofNullable(usersByEmail.get((String) args[0]));
                        case "existsByUsername" -> usersByUsername.containsKey((String) args[0]);
                        case "existsByEmail" -> usersByEmail.containsKey((String) args[0]);
                        case "save" -> save((User) args[0]);
                        default -> throw new UnsupportedOperationException(method.getName());
                    }
            );
        }

        User save(User user) {
            if (user.getId() == null) {
                user.setId(nextId++);
            }
            usersByUsername.put(user.getUsername(), user);
            usersByEmail.put(user.getEmail(), user);
            return user;
        }
    }
}
