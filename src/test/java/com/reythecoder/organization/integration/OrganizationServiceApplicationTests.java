package com.reythecoder.organization.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;

// import java.sql.Connection;
// import java.sql.DriverManager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@Tag("integration")
class OrganizationServiceApplicationTests {

	@Container
	static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:15-alpine")
			.withDatabaseName("organization_db")
			.withUsername("postgres")
			.withPassword("postgres")
			// .withInitScript("init_postgres.sql") // 是否要做数据库脚本目录映射
			.withEnv("POSTGRES_INITDB_ARGS", "--encoding=UTF8")
			.withReuse(true);

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
		// registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
	}

	@Test
	void test() {
		assertTrue(postgres.isRunning(), "PostgreSQL container should be running");
	}

	@Test
	void contextLoads() {
	}

}
