package com.ascend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Autowired;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@EnableScheduling
@EnableRetry
public class AscendApiApplication implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(AscendApiApplication.class);

	@Autowired
	private DataSource dataSource;

	public static void main(String[] args) {
		SpringApplication.run(AscendApiApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		logger.info("=== DATABASE CONNECTION INFO ===");
		logger.info("Database URL: {}", dataSource.getConnection().getMetaData().getURL());
		logger.info("Database Product: {}", dataSource.getConnection().getMetaData().getDatabaseProductName());
		logger.info("Database Version: {}", dataSource.getConnection().getMetaData().getDatabaseProductVersion());
		logger.info("================================");
	}

}
