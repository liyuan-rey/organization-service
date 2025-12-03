package com.reythecoder.organization.integration;

import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.MountableFile;

public abstract class AbstractContainerBase {

    public static final PostgreSQLContainer POSTGRES_CONTAINER;

    static {
        POSTGRES_CONTAINER = new PostgreSQLContainer("postgres:15-alpine")
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

        POSTGRES_CONTAINER.start();
    }
}
