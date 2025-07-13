package com.ascend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("!test")
public class DatabaseConfig {

    @Value("${DATABASE_URL:jdbc:postgresql://localhost:5432/ascend}")
    private String databaseUrl;

    @Bean
    @Primary
    public DataSource dataSource() {
        // Convert Railway's postgresql:// format to jdbc:postgresql://
        String jdbcUrl = databaseUrl;
        if (databaseUrl.startsWith("postgresql://")) {
            jdbcUrl = "jdbc:" + databaseUrl;
        }
        
        return DataSourceBuilder.create()
                .url(jdbcUrl)
                .driverClassName("org.postgresql.Driver")
                .build();
    }
} 