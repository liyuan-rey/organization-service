package com.reythecoder.organization.repository;

import com.reythecoder.organization.entity.EntityType;
import com.reythecoder.organization.entity.OrgTreeNodeEntity;
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

/**
 * Integration tests for OrgTreeNodeRepository.
 */
@Testcontainers
@DataJpaTest
@Tag("integration")
class OrgTreeNodeRepositoryTest {

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:15-alpine")
            .withDatabaseName("organization_db")
            .withUsername("postgres")
            .withPassword("postgres")
            .withInitScript("01-init-department-personnel-group-tables.sql")
            .withEnv("POSTGRES_INITDB_ARGS", "--encoding=UTF8")
            .withReuse(false);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private OrgTreeNodeRepository orgTreeNodeRepository;

    private UUID rootId;
    private UUID groupId;
    private UUID departmentId;
    private UUID personnelId;
    private UUID tenantId;
    private OffsetDateTime now;

    @BeforeEach
    void setUp() {
        orgTreeNodeRepository.deleteAll();

        tenantId = UUIDv7.randomUUID();
        now = OffsetDateTime.now();
        rootId = UUIDv7.randomUUID();
        groupId = UUIDv7.randomUUID();
        departmentId = UUIDv7.randomUUID();
        personnelId = UUIDv7.randomUUID();
    }

    @Test
    void findAll_shouldReturnAllNodes() {
        // Arrange
        OrgTreeNodeEntity rootNode = createRootNode();
        orgTreeNodeRepository.save(rootNode);

        // Act
        List<OrgTreeNodeEntity> nodes = orgTreeNodeRepository.findAll();

        // Assert
        assertThat(nodes).hasSize(1);
        assertThat(nodes.get(0)).isEqualTo(rootNode);
    }

    @Test
    void findById_shouldReturnNodeWhenExists() {
        // Arrange
        OrgTreeNodeEntity rootNode = createRootNode();
        orgTreeNodeRepository.save(rootNode);

        // Act
        Optional<OrgTreeNodeEntity> found = orgTreeNodeRepository.findById(rootId);

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(rootNode);
    }

    @Test
    void findById_shouldReturnEmptyWhenNotExists() {
        // Act
        Optional<OrgTreeNodeEntity> found = orgTreeNodeRepository.findById(UUIDv7.randomUUID());

        // Assert
        assertThat(found).isEmpty();
    }

    @Test
    void save_shouldSaveNode() {
        // Arrange
        OrgTreeNodeEntity node = createRootNode();

        // Act
        OrgTreeNodeEntity saved = orgTreeNodeRepository.save(node);

        // Assert
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(rootId);
        assertThat(saved.getEntityType()).isEqualTo(EntityType.ROOT);
    }

    @Test
    void delete_shouldDeleteNode() {
        // Arrange
        OrgTreeNodeEntity node = createRootNode();
        orgTreeNodeRepository.save(node);
        assertThat(orgTreeNodeRepository.findAll()).hasSize(1);

        // Act
        orgTreeNodeRepository.delete(node);

        // Assert
        assertThat(orgTreeNodeRepository.findAll()).isEmpty();
    }

    @Test
    void findByParentIdOrderBySortRankAsc_shouldReturnChildrenOrdered() {
        // Arrange
        OrgTreeNodeEntity rootNode = createRootNode();
        orgTreeNodeRepository.save(rootNode);

        // Create a non-root parent node
        UUID parentGroupId = UUIDv7.randomUUID();
        OrgTreeNodeEntity parentGroup = createNodeWithSortRank(parentGroupId, rootId, EntityType.GROUP, "a0", "Parent Group");
        orgTreeNodeRepository.save(parentGroup);

        // Create nodes with different sortRank values under the parent group
        OrgTreeNodeEntity child1 = createNodeWithSortRank(groupId, parentGroupId, EntityType.GROUP, "a2", "B");
        OrgTreeNodeEntity child2 = createNodeWithSortRank(departmentId, parentGroupId, EntityType.DEPARTMENT, "a1", "A");
        orgTreeNodeRepository.saveAll(List.of(child1, child2));

        // Act
        List<OrgTreeNodeEntity> children = orgTreeNodeRepository.findByParentIdOrderBySortRankAsc(parentGroupId);

        // Assert
        assertThat(children).hasSize(2);
        // Should be ordered by sortRank ascending
        assertThat(children.get(0).getAlias()).isEqualTo("A");
        assertThat(children.get(1).getAlias()).isEqualTo("B");
    }

    @Test
    void findByEntityTypeAndEntityId_shouldReturnNodeWhenExists() {
        // Arrange
        OrgTreeNodeEntity node = createNode(groupId, rootId, EntityType.GROUP, "Test Group");
        orgTreeNodeRepository.save(node);

        // Act
        Optional<OrgTreeNodeEntity> found = orgTreeNodeRepository.findByEntityTypeAndEntityId(EntityType.GROUP, groupId);

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(groupId);
    }

    @Test
    void findByEntityTypeAndEntityId_shouldReturnEmptyWhenNotExists() {
        // Act
        Optional<OrgTreeNodeEntity> found = orgTreeNodeRepository.findByEntityTypeAndEntityId(EntityType.GROUP, UUIDv7.randomUUID());

        // Assert
        assertThat(found).isEmpty();
    }

    @Test
    void findAllByEntityTypeAndEntityId_shouldReturnAllMatchingNodes() {
        // Arrange
        UUID sharedEntityId = UUIDv7.randomUUID();
        // Use different parent IDs to avoid unique constraint violation
        OrgTreeNodeEntity node1 = createNode(UUIDv7.randomUUID(), rootId, EntityType.GROUP, sharedEntityId, "Group 1");
        OrgTreeNodeEntity node2 = createNode(UUIDv7.randomUUID(), groupId, EntityType.GROUP, sharedEntityId, "Group 2");
        orgTreeNodeRepository.saveAll(List.of(node1, node2));

        // Act
        List<OrgTreeNodeEntity> nodes = orgTreeNodeRepository.findAllByEntityTypeAndEntityId(EntityType.GROUP, sharedEntityId);

        // Assert
        assertThat(nodes).hasSize(2);
    }

    @Test
    void countByParentId_shouldReturnCorrectCount() {
        // Arrange
        OrgTreeNodeEntity rootNode = createRootNode();
        orgTreeNodeRepository.save(rootNode);

        OrgTreeNodeEntity child1 = createNode(groupId, rootId, EntityType.GROUP, "Group 1");
        OrgTreeNodeEntity child2 = createNode(departmentId, rootId, EntityType.DEPARTMENT, "Department 1");
        orgTreeNodeRepository.saveAll(List.of(child1, child2));

        // Act
        long count = orgTreeNodeRepository.countByParentId(rootId);

        // Assert - count includes root node (which points to itself) + 2 children
        assertThat(count).isEqualTo(3);
    }

    @Test
    void findByParentIdAndEntityTypeAndEntityId_shouldReturnNodeWhenExists() {
        // Arrange
        OrgTreeNodeEntity node = createNode(groupId, rootId, EntityType.GROUP, "Test Group");
        orgTreeNodeRepository.save(node);

        // Act
        Optional<OrgTreeNodeEntity> found = orgTreeNodeRepository.findByParentIdAndEntityTypeAndEntityId(rootId, EntityType.GROUP, groupId);

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getAlias()).isEqualTo("Test Group");
    }

    @Test
    void countByParentIdAndEntityType_shouldReturnCorrectCount() {
        // Arrange
        OrgTreeNodeEntity rootNode = createRootNode();
        orgTreeNodeRepository.save(rootNode);

        OrgTreeNodeEntity group1 = createNode(UUIDv7.randomUUID(), rootId, EntityType.GROUP, "Group 1");
        OrgTreeNodeEntity group2 = createNode(UUIDv7.randomUUID(), rootId, EntityType.GROUP, "Group 2");
        OrgTreeNodeEntity dept = createNode(UUIDv7.randomUUID(), rootId, EntityType.DEPARTMENT, "Department 1");
        orgTreeNodeRepository.saveAll(List.of(group1, group2, dept));

        // Act
        long groupCount = orgTreeNodeRepository.countByParentIdAndEntityType(rootId, EntityType.GROUP);
        long deptCount = orgTreeNodeRepository.countByParentIdAndEntityType(rootId, EntityType.DEPARTMENT);

        // Assert
        assertThat(groupCount).isEqualTo(2);
        assertThat(deptCount).isEqualTo(1);
    }

    @Test
    void findByEntityType_shouldReturnAllNodesOfType() {
        // Arrange
        OrgTreeNodeEntity rootNode = createRootNode();
        OrgTreeNodeEntity groupNode = createNode(groupId, rootId, EntityType.GROUP, "Group 1");
        OrgTreeNodeEntity deptNode = createNode(departmentId, rootId, EntityType.DEPARTMENT, "Department 1");
        orgTreeNodeRepository.saveAll(List.of(rootNode, groupNode, deptNode));

        // Act
        List<OrgTreeNodeEntity> groups = orgTreeNodeRepository.findByEntityType(EntityType.GROUP);

        // Assert
        assertThat(groups).hasSize(1);
        assertThat(groups.get(0).getId()).isEqualTo(groupId);
    }

    @Test
    void findByLevel_shouldReturnAllNodesAtLevel() {
        // Arrange
        OrgTreeNodeEntity rootNode = createRootNode(); // level 0
        OrgTreeNodeEntity groupNode = createNode(groupId, rootId, EntityType.GROUP, 1, "Group 1"); // level 1
        orgTreeNodeRepository.saveAll(List.of(rootNode, groupNode));

        // Act
        List<OrgTreeNodeEntity> level0Nodes = orgTreeNodeRepository.findByLevel(0);
        List<OrgTreeNodeEntity> level1Nodes = orgTreeNodeRepository.findByLevel(1);

        // Assert
        assertThat(level0Nodes).hasSize(1);
        assertThat(level0Nodes.get(0).getId()).isEqualTo(rootId);
        assertThat(level1Nodes).hasSize(1);
        assertThat(level1Nodes.get(0).getId()).isEqualTo(groupId);
    }

    @Test
    void findAllDescendants_shouldReturnAllDescendants() {
        // Arrange
        OrgTreeNodeEntity rootNode = createRootNode();
        orgTreeNodeRepository.save(rootNode);

        UUID child1Id = UUIDv7.randomUUID();
        UUID child2Id = UUIDv7.randomUUID();
        // child1's path contains rootNode.id
        OrgTreeNodeEntity child1 = createNodeWithPath(child1Id, rootId, EntityType.GROUP, 1, "Child 1", new UUID[]{rootId});
        // child2's path contains rootNode.id and child1Id (it's a grandchild)
        OrgTreeNodeEntity child2 = createNodeWithPath(child2Id, child1Id, EntityType.DEPARTMENT, 2, "Child 2", new UUID[]{rootId, child1Id});
        orgTreeNodeRepository.saveAll(List.of(child1, child2));

        // Act
        List<OrgTreeNodeEntity> descendants = orgTreeNodeRepository.findAllDescendants(rootId);

        // Assert
        assertThat(descendants).hasSize(2);
        assertThat(descendants).extracting(OrgTreeNodeEntity::getId)
                .containsExactlyInAnyOrder(child1Id, child2Id);
    }

    @Test
    void findChildrenByParentId_shouldReturnDirectChildren() {
        // Arrange
        OrgTreeNodeEntity rootNode = createRootNode();
        orgTreeNodeRepository.save(rootNode);

        // Use a non-root parent to avoid the root node self-reference issue
        UUID parentGroupId = UUIDv7.randomUUID();
        OrgTreeNodeEntity parentGroup = createNodeWithPath(parentGroupId, rootId, EntityType.GROUP, 1, "Parent Group", new UUID[]{rootId});
        orgTreeNodeRepository.save(parentGroup);

        UUID child1Id = UUIDv7.randomUUID();
        UUID child2Id = UUIDv7.randomUUID();
        // child1 is a direct child of parentGroup
        OrgTreeNodeEntity child1 = createNodeWithPath(child1Id, parentGroupId, EntityType.GROUP, 2, "Child 1", new UUID[]{rootId, parentGroupId});
        // child2 is a child of child1 (grandchild of parentGroup)
        OrgTreeNodeEntity child2 = createNodeWithPath(child2Id, child1Id, EntityType.DEPARTMENT, 3, "Child 2", new UUID[]{rootId, parentGroupId, child1Id});
        orgTreeNodeRepository.saveAll(List.of(child1, child2));

        // Act
        List<OrgTreeNodeEntity> children = orgTreeNodeRepository.findChildrenByParentId(parentGroupId);

        // Assert
        assertThat(children).hasSize(1);
        assertThat(children.get(0).getId()).isEqualTo(child1Id);
    }

    @Test
    void findRootNode_shouldReturnRootNode() {
        // Arrange
        OrgTreeNodeEntity rootNode = createRootNode();
        orgTreeNodeRepository.save(rootNode);

        // Act
        Optional<OrgTreeNodeEntity> found = orgTreeNodeRepository.findRootNode();

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(rootId);
        assertThat(found.get().getLevel()).isEqualTo(0);
    }

    @Test
    void findChildrenByParentIds_shouldReturnChildrenForMultipleParents() {
        // Arrange
        UUID parent1Id = UUIDv7.randomUUID();
        UUID parent2Id = UUIDv7.randomUUID();
        UUID child1Id = UUIDv7.randomUUID();
        UUID child2Id = UUIDv7.randomUUID();

        OrgTreeNodeEntity child1 = createNode(child1Id, parent1Id, EntityType.GROUP, "Child 1");
        OrgTreeNodeEntity child2 = createNode(child2Id, parent2Id, EntityType.DEPARTMENT, "Child 2");
        orgTreeNodeRepository.saveAll(List.of(child1, child2));

        // Act
        List<OrgTreeNodeEntity> children = orgTreeNodeRepository.findChildrenByParentIds(List.of(parent1Id, parent2Id));

        // Assert
        assertThat(children).hasSize(2);
        assertThat(children).extracting(OrgTreeNodeEntity::getId)
                .containsExactlyInAnyOrder(child1Id, child2Id);
    }

    @Test
    void countChildrenByType_shouldReturnCorrectCount() {
        // Arrange
        OrgTreeNodeEntity rootNode = createRootNode();
        orgTreeNodeRepository.save(rootNode);

        orgTreeNodeRepository.saveAll(List.of(
                createNode(UUIDv7.randomUUID(), rootId, EntityType.GROUP, "Group 1"),
                createNode(UUIDv7.randomUUID(), rootId, EntityType.GROUP, "Group 2"),
                createNode(UUIDv7.randomUUID(), rootId, EntityType.DEPARTMENT, "Department 1")
        ));

        // Act
        long count = orgTreeNodeRepository.countChildrenByType(rootId);

        // Assert - includes root node (which points to itself) + 3 children
        assertThat(count).isEqualTo(4);
    }

    @Test
    void countByEntityTypeAndEntityId_shouldReturnCorrectCount() {
        // Arrange
        UUID entityId = UUIDv7.randomUUID();
        // Use different parent IDs to avoid unique constraint violation
        orgTreeNodeRepository.save(createNode(UUIDv7.randomUUID(), rootId, EntityType.GROUP, entityId, "Group 1"));
        orgTreeNodeRepository.save(createNode(UUIDv7.randomUUID(), groupId, EntityType.GROUP, entityId, "Group 2"));

        // Act
        long count = orgTreeNodeRepository.countByEntityTypeAndEntityId(EntityType.GROUP.name(), entityId);

        // Assert
        assertThat(count).isEqualTo(2);
    }

    // =====================================================
    // Helper methods
    // =====================================================

    private OrgTreeNodeEntity createRootNode() {
        return createNode(rootId, rootId, EntityType.ROOT, 0, "Root");
    }

    private OrgTreeNodeEntity createNode(UUID id, UUID parentId, EntityType entityType, String alias) {
        return createNode(id, parentId, entityType, 1, alias);
    }

    private OrgTreeNodeEntity createNode(UUID id, UUID parentId, EntityType entityType, int level, String alias) {
        return createNode(id, parentId, entityType, id, level, alias);
    }

    private OrgTreeNodeEntity createNode(UUID id, UUID parentId, EntityType entityType, UUID entityId, String alias) {
        return createNode(id, parentId, entityType, entityId, 1, alias);
    }

    private OrgTreeNodeEntity createNode(UUID id, UUID parentId, EntityType entityType, UUID entityId, int level, String alias) {
        UUID[] path;
        if (parentId.equals(id)) {
            path = new UUID[0];
        } else {
            path = new UUID[]{parentId};
        }

        return new OrgTreeNodeEntity(
                id,
                parentId,
                entityType,
                entityId,
                alias,
                level,
                path,
                "a1", // sortRank
                now,
                now,
                tenantId
        );
    }

    private OrgTreeNodeEntity createNodeWithSortRank(UUID id, UUID parentId, EntityType entityType, String sortRank, String alias) {
        UUID[] path = parentId.equals(id) ? new UUID[0] : new UUID[]{parentId};
        return new OrgTreeNodeEntity(
                id,
                parentId,
                entityType,
                id,
                alias,
                1,
                path,
                sortRank,
                now,
                now,
                tenantId
        );
    }

    private OrgTreeNodeEntity createNodeWithPath(UUID id, UUID parentId, EntityType entityType, int level, String alias, UUID[] path) {
        return new OrgTreeNodeEntity(
                id,
                parentId,
                entityType,
                id,
                alias,
                level,
                path,
                "a1",
                now,
                now,
                tenantId
        );
    }
}
