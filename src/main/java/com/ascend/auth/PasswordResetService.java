package com.ascend.auth;

import com.ascend.user.User;
import com.ascend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    private static final int TOKEN_EXPIRY_HOURS = 1;

    @Transactional
    public void requestPasswordReset(String email) {
        // Find user by email
        Optional<User> userOpt = userRepository.findByEmail(email.toLowerCase().trim());
        
        if (userOpt.isEmpty()) {
            log.warn("Password reset requested for non-existent email: {}", email);
            // Don't reveal if user exists or not for security
            return;
        }

        User user = userOpt.get();

        // Check if user already has a valid token
        if (tokenRepository.existsValidTokenForUser(user.getId(), LocalDateTime.now())) {
            log.info("User {} already has a valid password reset token", user.getEmail());
            // Don't create duplicate tokens
            return;
        }

        // Generate secure token
        String token = generateSecureToken();

        // Create password reset token
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .user(user)
                .token(token)
                .expiresAt(LocalDateTime.now().plusHours(TOKEN_EXPIRY_HOURS))
                .used(false)
                .createdAt(LocalDateTime.now())
                .build();

        tokenRepository.save(resetToken);

        // Send email
        try {
            emailService.sendPasswordResetEmail(user.getEmail(), token, user.getFirstName());
            log.info("Password reset token created and email sent for user: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send password reset email for user: {}", user.getEmail(), e);
            // Delete the token if email fails
            tokenRepository.delete(resetToken);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        // Find token
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByTokenAndUsedFalse(token);
        
        if (tokenOpt.isEmpty()) {
            log.warn("Password reset attempted with invalid token: {}", token);
            return false;
        }

        PasswordResetToken resetToken = tokenOpt.get();

        // Check if token is expired
        if (resetToken.isExpired()) {
            log.warn("Password reset attempted with expired token: {}", token);
            return false;
        }

        // Check if token is already used
        if (resetToken.isUsed()) {
            log.warn("Password reset attempted with already used token: {}", token);
            return false;
        }

        // Update user password
        User user = resetToken.getUser();
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        user.setPassword(hashedPassword);
        userRepository.save(user);

        // Mark token as used
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        // Mark all other tokens for this user as used
        tokenRepository.markAllTokensAsUsedForUser(user.getId());

        log.info("Password successfully reset for user: {}", user.getEmail());
        return true;
    }

    private String generateSecureToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    @Scheduled(cron = "0 0 */6 * * *") // Run every 6 hours
    @Transactional
    public void cleanupExpiredTokens() {
        try {
            LocalDateTime now = LocalDateTime.now();
            tokenRepository.deleteExpiredAndUsedTokens(now);
            log.info("Cleaned up expired and used password reset tokens");
        } catch (Exception e) {
            log.error("Failed to cleanup expired tokens", e);
        }
    }
} 