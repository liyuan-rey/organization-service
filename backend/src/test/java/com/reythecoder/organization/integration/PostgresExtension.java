package com.reythecoder.organization.integration;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.junit.jupiter.api.extension.BeforeAllCallback;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.postgresql.PostgreSQLContainer;

public class PostgresExtension implements BeforeAllCallback {

    @Container
    private static final PostgreSQLContainer container = new PostgreSQLContainer("postgres:15-alpine")
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

    static {
        container.start(); // 静态初始化时启动
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        // 将连接信息注入系统属性
        System.setProperty("DB_URL", container.getJdbcUrl());
        System.setProperty("DB_USER", container.getUsername());
        System.setProperty("DB_PASSWORD", container.getPassword());
    }

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
        // registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    public static void stopContainer() {
        container.stop();
    }
}
