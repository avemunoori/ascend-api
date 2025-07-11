package com.ascend.auth;

import com.ascend.user.User;
import com.ascend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        assertNotNull(token.getCode());
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

        // Get the code
        var tokens = tokenRepository.findAll();
        assertFalse(tokens.isEmpty());
        String resetCode = tokens.get(0).getCode();

        // Test password reset
        boolean success = passwordResetService.resetPassword(resetCode, "newpassword123");
        assertTrue(success);

        // Verify token is marked as used
        var updatedTokens = tokenRepository.findAll();
        assertTrue(updatedTokens.get(0).isUsed());
    }

    @Test
    public void testPasswordResetWithInvalidCode() {
        boolean success = passwordResetService.resetPassword("123456", "newpassword123");
        assertFalse(success);
    }

    @Test
    public void testEmailTemplateGeneration() {
        // Get the real EmailService (not mocked)
        EmailService realEmailService = new EmailService(mock(JavaMailSender.class));
        ReflectionTestUtils.setField(realEmailService, "fromEmail", "noreply@ascendclimbing.xyz");
        ReflectionTestUtils.setField(realEmailService, "frontendUrl", "exp://localhost:8081");

        // Test that the email template contains expected content
        String template = (String) ReflectionTestUtils.invokeMethod(realEmailService, 
            "createPasswordResetEmailTemplate", "John", "123456");
        
        assertNotNull(template);
        assertTrue(template.contains("Hello John"));
        assertTrue(template.contains("123456"));
        assertTrue(template.contains("noreply@ascendclimbing.xyz"));
        assertTrue(template.contains("Ascend Climbing"));
        assertTrue(template.contains("This code will expire in 15 minutes"));
    }
} 