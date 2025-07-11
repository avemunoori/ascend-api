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

    private static final int CODE_EXPIRY_MINUTES = 15; // Shorter expiry for codes
    private static final int MAX_ATTEMPTS = 3; // Maximum attempts for code verification

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

        // Generate 6-digit code
        String code = generateSecureCode();

        // Create password reset token
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .user(user)
                .code(code)
                .expiresAt(LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES))
                .used(false)
                .attempts(0)
                .createdAt(LocalDateTime.now())
                .build();

        tokenRepository.save(resetToken);

        // Send email
        try {
            emailService.sendPasswordResetEmail(user.getEmail(), code, user.getFirstName());
            log.info("Password reset code created and email sent for user: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send password reset email for user: {}", user.getEmail(), e);
            // Delete the token if email fails
            tokenRepository.delete(resetToken);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    @Transactional
    public boolean resetPassword(String code, String newPassword) {
        // Find token by code
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByCodeAndUsedFalse(code);
        
        if (tokenOpt.isEmpty()) {
            log.warn("Password reset attempted with invalid code: {}", code);
            return false;
        }

        PasswordResetToken resetToken = tokenOpt.get();

        // Check if code is expired
        if (resetToken.isExpired()) {
            log.warn("Password reset attempted with expired code: {}", code);
            return false;
        }

        // Check if code is already used
        if (resetToken.isUsed()) {
            log.warn("Password reset attempted with already used code: {}", code);
            return false;
        }

        // Check if too many attempts
        if (resetToken.getAttempts() >= MAX_ATTEMPTS) {
            log.warn("Password reset attempted with code that has exceeded max attempts: {}", code);
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

    @Transactional
    public boolean verifyCode(String code) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByCodeAndUsedFalse(code);
        
        if (tokenOpt.isEmpty()) {
            log.warn("Code verification attempted with invalid code: {}", code);
            return false;
        }

        PasswordResetToken resetToken = tokenOpt.get();

        // Check if code is expired
        if (resetToken.isExpired()) {
            log.warn("Code verification attempted with expired code: {}", code);
            return false;
        }

        // Check if code is already used
        if (resetToken.isUsed()) {
            log.warn("Code verification attempted with already used code: {}", code);
            return false;
        }

        // Increment attempts
        resetToken.setAttempts(resetToken.getAttempts() + 1);
        tokenRepository.save(resetToken);

        // Check if too many attempts
        if (resetToken.getAttempts() >= MAX_ATTEMPTS) {
            log.warn("Code verification failed - too many attempts for code: {}", code);
            return false;
        }

        log.info("Code verified successfully for user: {}", resetToken.getUser().getEmail());
        return true;
    }

    private String generateSecureCode() {
        // Generate a 6-digit numeric code
        return String.format("%06d", (int) (Math.random() * 1000000));
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