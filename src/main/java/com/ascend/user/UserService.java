package com.ascend.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public User createUser(String email, String password) {
        log.info("Creating user with email: {}", email);
        try {
            String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
            User user = userRepository.save(
                User.builder()
                    .email(email)
                    .password(hashed)
                    .createdAt(java.time.LocalDateTime.now())
                    .build()
            );
            log.info("Successfully created user with ID: {}", user.getId());
            return user;
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
