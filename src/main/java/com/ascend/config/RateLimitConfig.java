package com.ascend.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitConfig {

    @Bean
    public Bucket forgotPasswordBucket() {
        // Allow 3 requests per hour per IP
        Bandwidth limit = Bandwidth.classic(3, Refill.greedy(3, Duration.ofHours(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
} 