package com.ascend.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.ActiveProfiles;
import jakarta.mail.internet.MimeMessage;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.mail.host=in-v3.mailjet.com",
    "spring.mail.port=587",
    "spring.mail.username=27ed38a02b4313f83e5f804394a4f273",
    "spring.mail.password=72d6fe739f72fc8d5a6a7a67dacbe811",
    "spring.mail.protocol=smtp",
    "spring.mail.properties.mail.smtp.auth=true",
    "spring.mail.properties.mail.smtp.starttls.enable=true",
    "spring.mail.properties.mail.smtp.starttls.required=true",
    "app.mail.from=noreply@ascendclimbing.xyz",
    "app.frontend.url=http://localhost:3000"
})
public class MailjetIntegrationTest {

    @Autowired
    private EmailService emailService;

    @Autowired
    private JavaMailSender mailSender;

    @Test
    public void testMailjetConnection() {
        // Test basic connection
        assertNotNull(mailSender);
        
        // Test that we can create a message
        MimeMessage message = mailSender.createMimeMessage();
        assertNotNull(message);
    }

    @Test
    public void testPasswordResetEmailSending() {
        // This test will actually send an email - use with caution
        // You can comment this out if you don't want to send real emails during testing
        assertDoesNotThrow(() -> {
            emailService.sendPasswordResetEmail(
                "test@example.com", 
                "test-token-123", 
                "Test User"
            );
        });
    }
} 