package com.ascend.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.MessagingException;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Retryable(
        value = {MailSendException.class, MessagingException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void sendPasswordResetEmail(String toEmail, String resetCode, String firstName) {
        log.info("Attempting to send password reset email to: {} for user: {}", toEmail, firstName);
        
        // Validate inputs
        if (toEmail == null || toEmail.trim().isEmpty()) {
            log.error("Email address is null or empty");
            throw new IllegalArgumentException("Email address cannot be null or empty");
        }
        
        if (resetCode == null || resetCode.trim().isEmpty()) {
            log.error("Reset code is null or empty");
            throw new IllegalArgumentException("Reset code cannot be null or empty");
        }
        
        if (firstName == null || firstName.trim().isEmpty()) {
            log.warn("First name is null or empty, using 'User' as default");
            firstName = "User";
        }
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            // Set sender using verified domain
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Reset Your Ascend Climbing Password");

            log.debug("Reset code: {}", resetCode);
            
            String htmlContent = createPasswordResetEmailTemplate(firstName, resetCode);
            helper.setText(htmlContent, true);
            
            log.info("Sending email via Mailjet SMTP...");
            mailSender.send(message);
            
            log.info("✅ Password reset email sent successfully to: {} with reset code: {}", toEmail, resetCode);
        } catch (MailAuthenticationException e) {
            log.error("❌ SMTP authentication failed for Mailjet: {}", e.getMessage(), e);
            throw new RuntimeException("Email service authentication failed. Please check Mailjet API credentials.", e);
        } catch (MailSendException e) {
            log.error("❌ Failed to send email via Mailjet: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send email. Please try again later.", e);
        } catch (MessagingException e) {
            log.error("❌ Email messaging error: {}", e.getMessage(), e);
            throw new RuntimeException("Email formatting error", e);
        } catch (Exception e) {
            log.error("❌ Unexpected error while sending email: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error while sending email", e);
        }
    }

    private String createPasswordResetEmailTemplate(String firstName, String resetCode) {
        return String.format(
            """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Password Reset - Ascend Climbing</title>
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .button { display: inline-block; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 15px 30px; text-decoration: none; border-radius: 25px; font-weight: bold; margin: 20px 0; }
                    .button:hover { background: linear-gradient(135deg, #5a6fd8 0%%, #6a4190 100%%); }
                    .footer { text-align: center; margin-top: 30px; color: #666; font-size: 14px; }
                    .warning { background: #fff3cd; border: 1px solid #ffeaa7; padding: 15px; border-radius: 5px; margin: 20px 0; }
                    .logo { font-size: 24px; font-weight: bold; margin-bottom: 10px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="logo">🧗 Ascend Climbing</div>
                        <h1>Password Reset Code</h1>
                    </div>
                    <div class="content">
                        <h2>Hello %s,</h2>
                        <p>We received a request to reset your password for your Ascend Climbing account.</p>
                        <p>Use the code below to reset your password:</p>
                        
                        <div style="text-align: center; margin: 30px 0;">
                            <div style="background: #f8f9fa; border: 2px solid #667eea; border-radius: 10px; padding: 20px; display: inline-block;">
                                <h1 style="color: #667eea; font-size: 48px; font-weight: bold; margin: 0; letter-spacing: 8px;">%s</h1>
                            </div>
                        </div>
                        
                        <div class="warning">
                            <strong>⚠️ Security Notice:</strong>
                            <ul>
                                <li>This code will expire in 15 minutes</li>
                                <li>You have 3 attempts to enter the code correctly</li>
                                <li>If you didn't request this reset, please ignore this email</li>
                                <li>Your password will remain unchanged until you use this code</li>
                            </ul>
                        </div>
                        
                        <p><strong>How to use this code:</strong></p>
                        <ol>
                            <li>Open your Ascend Climbing app</li>
                            <li>Go to the password reset screen</li>
                            <li>Enter the 6-digit code above</li>
                            <li>Create your new password</li>
                        </ol>
                        
                        <p>Need help? Contact our support team at <a href="mailto:support@ascendclimbing.xyz">support@ascendclimbing.xyz</a></p>
                    </div>
                    <div class="footer">
                        <p>© 2024 Ascend Climbing. All rights reserved.</p>
                        <p>This email was sent from noreply@ascendclimbing.xyz</p>
                    </div>
                </div>
            </body>
            </html>
            """,
            firstName, resetCode
        );
    }
    
    /**
     * Test method to verify email configuration
     */
    public void testEmailConfiguration() {
        log.info("Testing email configuration...");
        log.info("From email: {}", fromEmail);
        log.info("Frontend URL: {}", frontendUrl);
        log.info("Mailjet host: in-v3.mailjet.com");
        log.info("Mailjet port: 587");
        
        try {
            // Test if we can create a message
            MimeMessage message = mailSender.createMimeMessage();
            log.info("✅ Successfully created MimeMessage");
            
            // Test if we can set basic properties
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            log.info("✅ Successfully set from email: {}", fromEmail);
            
        } catch (Exception e) {
            log.error("❌ Email configuration test failed: {}", e.getMessage(), e);
            throw new RuntimeException("Email configuration test failed", e);
        }
        
        log.info("✅ Email configuration test passed");
    }
} 