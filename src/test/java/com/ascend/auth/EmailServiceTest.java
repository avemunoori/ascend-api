package com.ascend.auth;

import com.ascend.user.User;
import com.ascend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(com.ascend.config.TestConfig.class)
public class EmailServiceTest {

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @BeforeEach
    public void setUp() {
        tokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testPasswordResetFlow() {
        // Create a test user
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("$2a$10$dummy.hash.for.testing");
        user.setFirstName("Test");
        user.setLastName("User");
        userRepository.save(user);

        // Test password reset request
        assertDoesNotThrow(() -> {
            passwordResetService.requestPasswordReset("test@example.com");
        });

        // Verify token was created
        assertTrue(tokenRepository.count() > 0);
        
        // Get the created token
        var tokens = tokenRepository.findAll();
        assertFalse(tokens.isEmpty());
        
        PasswordResetToken token = tokens.get(0);
        assertNotNull(token.getToken());
        assertFalse(token.isUsed());
        assertFalse(token.isExpired());
        assertEquals(user.getId(), token.getUser().getId());
    }

    @Test
    public void testPasswordResetWithValidToken() {
        // Create a test user
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("$2a$10$dummy.hash.for.testing");
        user.setFirstName("Test");
        user.setLastName("User");
        userRepository.save(user);

        // Request password reset
        passwordResetService.requestPasswordReset("test@example.com");

        // Get the token
        var tokens = tokenRepository.findAll();
        assertFalse(tokens.isEmpty());
        String resetToken = tokens.get(0).getToken();

        // Test password reset
        boolean success = passwordResetService.resetPassword(resetToken, "newpassword123");
        assertTrue(success);

        // Verify token is marked as used
        var updatedTokens = tokenRepository.findAll();
        assertTrue(updatedTokens.get(0).isUsed());
    }

    @Test
    public void testPasswordResetWithInvalidToken() {
        boolean success = passwordResetService.resetPassword("invalid-token", "newpassword123");
        assertFalse(success);
    }
} 