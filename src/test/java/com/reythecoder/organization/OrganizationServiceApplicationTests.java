package com.reythecoder.organization;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@Testcontainers
@SpringBootTest
@Tag("integration")
class OrganizationServiceApplicationTests {

	@Container
	static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:15-alpine")
			.withDatabaseName("organization_db")
			.withUsername("postgres")
			.withPassword("postgres")
			// .withInitScript(
			// MountableFile.forClasspathResource("init_postgres.sql"),
			// "/docker-entrypoint-initdb.d/init.sql")
			// .withInitScript("file:db/init-scripts/01-init-department-personnel-group-tables.sql")
			.withInitScript("01-init-department-personnel-group-tables.sql")
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
	void db_shouldBeRunning() {
		assertTrue(postgres.isRunning(), "PostgreSQL container should be running");
	}

	@Test
	void contextLoads() {
		assertTrue(true, "Context should load successfully");
	}

}
