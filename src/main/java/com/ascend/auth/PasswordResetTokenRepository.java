package com.ascend.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    
    Optional<PasswordResetToken> findByCode(String code);
    
    Optional<PasswordResetToken> findByCodeAndUsedFalse(String code);
    
    @Query("SELECT COUNT(p) > 0 FROM PasswordResetToken p WHERE p.user.id = :userId AND p.used = false AND p.expiresAt > :now")
    boolean existsValidTokenForUser(@Param("userId") UUID userId, @Param("now") LocalDateTime now);
    
    @Modifying
    @Query("DELETE FROM PasswordResetToken p WHERE p.expiresAt < :now OR p.used = true")
    void deleteExpiredAndUsedTokens(@Param("now") LocalDateTime now);
    
    @Modifying
    @Query("UPDATE PasswordResetToken p SET p.used = true WHERE p.user.id = :userId AND p.used = false")
    void markAllTokensAsUsedForUser(@Param("userId") UUID userId);
} 