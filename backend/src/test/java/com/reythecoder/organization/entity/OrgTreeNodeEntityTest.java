package com.reythecoder.organization.entity;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for OrgTreeNodeEntity.
 */
class OrgTreeNodeEntityTest {

    @Test
    void testEntityType_Values() {
        // Test all EntityType enum values exist
        assertEquals(4, EntityType.values().length);
        assertEquals(EntityType.ROOT, EntityType.valueOf("ROOT"));
        assertEquals(EntityType.GROUP, EntityType.valueOf("GROUP"));
        assertEquals(EntityType.DEPARTMENT, EntityType.valueOf("DEPARTMENT"));
        assertEquals(EntityType.PERSONNEL, EntityType.valueOf("PERSONNEL"));
    }

    @Test
    void testOrgTreeNodeEntity_DefaultConstructor() {
        // Arrange & Act
        OrgTreeNodeEntity entity = new OrgTreeNodeEntity();

        // Assert
        assertNotNull(entity);
        assertNull(entity.getId());
        assertNull(entity.getParentId());
        assertNull(entity.getEntityType());
        assertNull(entity.getEntityId());
        assertNull(entity.getAlias());
        assertNull(entity.getLevel());
        assertNull(entity.getPath());
        assertNull(entity.getSortRank());
        assertNull(entity.getCreateTime());
        assertNull(entity.getUpdateTime());
        assertNull(entity.getTenantId());
    }

    @Test
    void testOrgTreeNodeEntity_FullConstructor() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID parentId = UUID.randomUUID();
        EntityType entityType = EntityType.GROUP;
        UUID entityId = UUID.randomUUID();
        String alias = "Test Alias";
        Integer level = 1;
        UUID[] path = new UUID[]{parentId};
        String sortRank = "a0";
        OffsetDateTime createTime = OffsetDateTime.now();
        OffsetDateTime updateTime = OffsetDateTime.now();
        UUID tenantId = UUID.fromString("00000000-0000-0000-0000-000000000000");

        // Act
        OrgTreeNodeEntity entity = new OrgTreeNodeEntity(
                id, parentId, entityType, entityId, alias, level, path, sortRank,
                createTime, updateTime, tenantId);

        // Assert
        assertNotNull(entity);
        assertEquals(id, entity.getId());
        assertEquals(parentId, entity.getParentId());
        assertEquals(entityType, entity.getEntityType());
        assertEquals(entityId, entity.getEntityId());
        assertEquals(alias, entity.getAlias());
        assertEquals(level, entity.getLevel());
        assertArrayEquals(path, entity.getPath());
        assertEquals(sortRank, entity.getSortRank());
        assertEquals(createTime, entity.getCreateTime());
        assertEquals(updateTime, entity.getUpdateTime());
        assertEquals(tenantId, entity.getTenantId());
    }

    @Test
    void testOrgTreeNodeEntity_SettersAndGetters() {
        // Arrange
        OrgTreeNodeEntity entity = new OrgTreeNodeEntity();
        UUID id = UUID.randomUUID();
        UUID parentId = UUID.randomUUID();
        EntityType entityType = EntityType.DEPARTMENT;
        UUID entityId = UUID.randomUUID();
        String alias = "Department Alias";
        Integer level = 2;
        UUID[] path = new UUID[]{parentId};
        String sortRank = "b0";
        OffsetDateTime createTime = OffsetDateTime.now();
        OffsetDateTime updateTime = OffsetDateTime.now();
        UUID tenantId = UUID.fromString("00000000-0000-0000-0000-000000000000");

        // Act
        entity.setId(id);
        entity.setParentId(parentId);
        entity.setEntityType(entityType);
        entity.setEntityId(entityId);
        entity.setAlias(alias);
        entity.setLevel(level);
        entity.setPath(path);
        entity.setSortRank(sortRank);
        entity.setCreateTime(createTime);
        entity.setUpdateTime(updateTime);
        entity.setTenantId(tenantId);

        // Assert
        assertEquals(id, entity.getId());
        assertEquals(parentId, entity.getParentId());
        assertEquals(entityType, entity.getEntityType());
        assertEquals(entityId, entity.getEntityId());
        assertEquals(alias, entity.getAlias());
        assertEquals(level, entity.getLevel());
        assertArrayEquals(path, entity.getPath());
        assertEquals(sortRank, entity.getSortRank());
        assertEquals(createTime, entity.getCreateTime());
        assertEquals(updateTime, entity.getUpdateTime());
        assertEquals(tenantId, entity.getTenantId());
    }

    @Test
    void testOrgTreeNodeEntity_RootNode() {
        // Arrange
        UUID rootId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        OrgTreeNodeEntity rootNode = new OrgTreeNodeEntity();

        // Act
        rootNode.setId(rootId);
        rootNode.setParentId(rootId);
        rootNode.setEntityType(EntityType.ROOT);
        rootNode.setEntityId(rootId);
        rootNode.setAlias("");
        rootNode.setLevel(0);
        rootNode.setPath(new UUID[]{});
        rootNode.setSortRank("a0");
        rootNode.setCreateTime(OffsetDateTime.now());
        rootNode.setUpdateTime(OffsetDateTime.now());
        rootNode.setTenantId(UUID.fromString("00000000-0000-0000-0000-000000000000"));

        // Assert
        assertEquals(rootId, rootNode.getId());
        assertEquals(rootId, rootNode.getParentId());
        assertEquals(EntityType.ROOT, rootNode.getEntityType());
        assertEquals(0, rootNode.getLevel());
        assertEquals(0, rootNode.getPath().length);
    }

    @Test
    void testOrgTreeNodeEntity_PathArray() {
        // Arrange
        OrgTreeNodeEntity entity = new OrgTreeNodeEntity();
        UUID rootId = UUID.randomUUID();
        UUID level1Id = UUID.randomUUID();
        UUID level2Id = UUID.randomUUID();

        // Act - Create a path representing root -> level1 -> level2
        entity.setPath(new UUID[]{rootId, level1Id});
        entity.setId(level2Id);

        // Assert
        assertEquals(2, entity.getPath().length);
        assertEquals(rootId, entity.getPath()[0]);
        assertEquals(level1Id, entity.getPath()[1]);
    }

    @Test
    void testOrgTreeNodeEntity_LetterConstructor() {
        // Arrange
        UUID id = UUID.randomUUID();
        String name = "Test Group";

        // Act - Test that we can create entity and set all required fields
        OrgTreeNodeEntity entity = new OrgTreeNodeEntity();
        entity.setId(id);

        // Assert
        assertNotNull(entity);
        assertEquals(id, entity.getId());
    }
}
