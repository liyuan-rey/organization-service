package com.reythecoder.organization.integration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class MyTestConfiguration {

    @Bean
    PostgreSQLContainer postgreSqlContainer() {
        return new PostgreSQLContainer("postgres:15-alpine")
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
    }

}
