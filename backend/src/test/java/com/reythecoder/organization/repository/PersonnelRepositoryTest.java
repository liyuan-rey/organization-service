package com.reythecoder.organization.repository;

import com.reythecoder.organization.entity.PersonnelEntity;
import io.github.robsonkades.uuidv7.UUIDv7;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
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

@Testcontainers
@DataJpaTest
@Tag("integration")
class PersonnelRepositoryTest {

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:15-alpine")
            .withDatabaseName("organization_db")
            .withUsername("postgres")
            .withPassword("postgres")
            .withInitScript("01-init-department-personnel-group-tables.sql")
            .withEnv("POSTGRES_INITDB_ARGS", "--encoding=UTF8")
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private PersonnelRepository personnelRepository;

    private UUID personnelId;
    private PersonnelEntity personnelEntity;

    @BeforeEach
    void setUp() {
        // 清理数据库
        personnelRepository.deleteAll();

        personnelId = UUIDv7.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        UUID tenantId = UUIDv7.randomUUID();

        personnelEntity = new PersonnelEntity(
                personnelId,
                "张三",
                "M",
                "110101199001011234",
                "13800138000",
                "010-12345678",
                "010-87654321",
                "zhangsan@example.com",
                null, // photo
                now,
                now,
                tenantId,
                false
        );
    }

    @Test
    void findAll_shouldReturnAllPersonnel() {
        // Arrange
        personnelRepository.save(personnelEntity);

        // Act
        List<PersonnelEntity> personnelList = personnelRepository.findAll();

        // Assert
        assertThat(personnelList).hasSize(1);
        assertThat(personnelList.get(0)).isEqualTo(personnelEntity);
    }

    @Test
    void findById_shouldReturnPersonnelWhenExists() {
        // Arrange
        personnelRepository.save(personnelEntity);

        // Act
        Optional<PersonnelEntity> found = personnelRepository.findById(personnelId);

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(personnelEntity);
    }

    @Test
    void save_shouldSavePersonnel() {
        // Act
        PersonnelEntity saved = personnelRepository.save(personnelEntity);

        // Assert
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(personnelId);
        assertThat(saved.getName()).isEqualTo(personnelEntity.getName());
    }

    @Test
    void delete_shouldDeletePersonnel() {
        // Arrange
        personnelRepository.save(personnelEntity);
        assertThat(personnelRepository.findAll()).hasSize(1);

        // Act
        personnelRepository.delete(personnelEntity);

        // Assert
        assertThat(personnelRepository.findAll()).isEmpty();
    }
}