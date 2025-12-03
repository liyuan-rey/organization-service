package com.reythecoder.organization.repository;

import com.reythecoder.organization.entity.DepartmentEntity;

import io.github.robsonkades.uuidv7.UUIDv7;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@DataJpaTest
@Tag("integration")
class DepartmentRepositoryTest {

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

    @Autowired
    private DepartmentRepository departmentRepository;

    private UUID departmentId;
    private DepartmentEntity departmentEntity;

    @BeforeEach
    void setUp() {
        // 清理数据库
        departmentRepository.deleteAll();

        departmentId = UUIDv7.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        UUID tenantId = UUIDv7.randomUUID();

        departmentEntity = new DepartmentEntity(
                departmentId,
                "测试部门",
                "Test Department",
                "测试部",
                "TEST001",
                "123456789",
                "987654321",
                "tes e.com",
                "测试地址", "123456",
                now, now,
                tenantId);
    }

    @Test
    void findAll_shouldReturnAllDepartments() {
        // Arrange
        departmentRepository.save(departmentEntity);

        // Act
        List<DepartmentEntity> departments = departmentRepository.findAll();

        // Assert
        assertThat(departments).hasSize(1);
        assertThat(departments.get(0)).isEqualTo(departmentEntity);
    }

    @Test
    void findAll_shouldReturnEmptyListWhenNoDepartments() {
        // Act
        List<DepartmentEntity> departments = departmentRepository.findAll();

        // Assert
        assertThat(departments).isEmpty();
    }

    @Test
    void findById_shouldReturnDepartmentWhenExists() {
        // Arrange
        departmentRepository.save(departmentEntity);

        // Act
        Optional<DepartmentEntity> found = departmentRepository.findById(departmentId);

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(departmentEntity);
    }

    @Test
    void findById_shouldReturnEmptyWhenDepartmentNotFound() {
        // Act
        Optional<DepartmentEntity> found = departmentRepository.findById(departmentId);

        // Assert
        assertThat(found).isEmpty();
    }

    @Test
    void save_shouldSaveDepartment() {
        // Act
        DepartmentEntity saved = departmentRepository.save(departmentEntity);

        // Assert
        assertThat(saved).isNotNull();
        assertThat(saved.id()).isEqualTo(departmentId);
        assertThat(saved.name()).isEqualTo(departmentEntity.name());
    }

    @Test
    void save_shouldGenerateIdWhenNull() {
        // Arrange
        DepartmentEntity entityWithoutId = new DepartmentEntity(
                null, // 不提供ID
                "测试部门",
                "Test Department",
                "测试部",
                "TEST001",
                "123456789",
                "987654321",
                "test@example.com",
                "测试地址",
                "123456",
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                UUIDv7.randomUUID());

        // Act
        DepartmentEntity saved = departmentRepository.save(entityWithoutId);

        // Assert
        assertThat(saved).isNotNull();
        assertThat(saved.id()).isNotNull(); // 应该生成ID
        assertThat(saved.name()).isEqualTo(entityWithoutId.name());
    }

    @Test
    void delete_shouldDeleteDepartment() {
        // Arrange
        departmentRepository.save(departmentEntity);
        assertThat(departmentRepository.findAll()).hasSize(1);

        // Act
        departmentRepository.delete(departmentEntity);

        // Assert
        assertThat(departmentRepository.findAll()).isEmpty();
    }

    @Test
    void deleteById_shouldDeleteDepartment() {
        // Arrange
        departmentRepository.save(departmentEntity);
        assertThat(departmentRepository.findAll()).hasSize(1);

        // Act
        departmentRepository.deleteById(departmentId);

        // Assert
        assertThat(departmentRepository.findAll()).isEmpty();
    }
}
