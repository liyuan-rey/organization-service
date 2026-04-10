package com.reythecoder.organization.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.reythecoder.organization.dto.request.CreateTreeNodeReq;
import com.reythecoder.organization.dto.request.MoveTreeNodeReq;
import com.reythecoder.organization.dto.request.UpdateTreeNodeReq;
import com.reythecoder.organization.dto.response.TreeStatistics;
import com.reythecoder.organization.dto.response.TreeNodeRsp;
import com.reythecoder.organization.entity.EntityType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

/**
 * Unit tests for DTO classes.
 */
@DisplayName("DTO Tests")
class DtoTest {

    @Test
    @DisplayName("CreateTreeNodeReq - Test builder pattern")
    void testCreateTreeNodeReqBuilder() {
        UUID parentId = UUID.randomUUID();
        UUID entityId = UUID.randomUUID();
        String alias = "Test Node";
        String sortRank = "abc123";

        CreateTreeNodeReq req = CreateTreeNodeReq.builder()
                .parentId(parentId)
                .entityType(EntityType.DEPARTMENT)
                .entityId(entityId)
                .alias(alias)
                .sortRank(sortRank)
                .build();

        assertNotNull(req);
        assertEquals(parentId, req.getParentId());
        assertEquals(EntityType.DEPARTMENT, req.getEntityType());
        assertEquals(entityId, req.getEntityId());
        assertEquals(alias, req.getAlias());
        assertEquals(sortRank, req.getSortRank());
    }

    @Test
    @DisplayName("CreateTreeNodeReq - Test constructor")
    void testCreateTreeNodeReqConstructor() {
        UUID parentId = UUID.randomUUID();
        UUID entityId = UUID.randomUUID();
        String alias = "Test Node";
        String sortRank = "abc123";

        CreateTreeNodeReq req = new CreateTreeNodeReq(parentId, EntityType.GROUP, entityId, alias, sortRank);

        assertNotNull(req);
        assertEquals(parentId, req.getParentId());
        assertEquals(EntityType.GROUP, req.getEntityType());
        assertEquals(entityId, req.getEntityId());
        assertEquals(alias, req.getAlias());
        assertEquals(sortRank, req.getSortRank());
    }

    @Test
    @DisplayName("UpdateTreeNodeReq - Test builder pattern")
    void testUpdateTreeNodeReqBuilder() {
        UUID id = UUID.randomUUID();
        String alias = "Updated Node";
        String sortRank = "xyz789";

        UpdateTreeNodeReq req = UpdateTreeNodeReq.builder()
                .id(id)
                .alias(alias)
                .sortRank(sortRank)
                .build();

        assertNotNull(req);
        assertEquals(id, req.getId());
        assertEquals(alias, req.getAlias());
        assertEquals(sortRank, req.getSortRank());
    }

    @Test
    @DisplayName("UpdateTreeNodeReq - Test constructor")
    void testUpdateTreeNodeReqConstructor() {
        UUID id = UUID.randomUUID();
        String alias = "Updated Node";
        String sortRank = "xyz789";

        UpdateTreeNodeReq req = new UpdateTreeNodeReq(id, alias, sortRank);

        assertNotNull(req);
        assertEquals(id, req.getId());
        assertEquals(alias, req.getAlias());
        assertEquals(sortRank, req.getSortRank());
    }

    @Test
    @DisplayName("MoveTreeNodeReq - Test builder pattern")
    void testMoveTreeNodeReqBuilder() {
        UUID nodeId = UUID.randomUUID();
        UUID newParentId = UUID.randomUUID();
        String newSortRank = "def456";

        MoveTreeNodeReq req = MoveTreeNodeReq.builder()
                .nodeId(nodeId)
                .newParentId(newParentId)
                .newSortRank(newSortRank)
                .build();

        assertNotNull(req);
        assertEquals(nodeId, req.getNodeId());
        assertEquals(newParentId, req.getNewParentId());
        assertEquals(newSortRank, req.getNewSortRank());
    }

    @Test
    @DisplayName("MoveTreeNodeReq - Test constructor")
    void testMoveTreeNodeReqConstructor() {
        UUID nodeId = UUID.randomUUID();
        UUID newParentId = UUID.randomUUID();
        String newSortRank = "def456";

        MoveTreeNodeReq req = new MoveTreeNodeReq(nodeId, newParentId, newSortRank);

        assertNotNull(req);
        assertEquals(nodeId, req.getNodeId());
        assertEquals(newParentId, req.getNewParentId());
        assertEquals(newSortRank, req.getNewSortRank());
    }

    @Test
    @DisplayName("TreeNodeRsp - Test builder pattern")
    void testTreeNodeRspBuilder() {
        UUID id = UUID.randomUUID();
        String name = "Test Node";
        Integer sortOrder = 1;
        TreeStatistics statistics = new TreeStatistics(2, 3, 5);
        List<TreeNodeRsp> children = List.of();

        TreeNodeRsp rsp = TreeNodeRsp.builder()
                .id(id)
                .type(EntityType.DEPARTMENT)
                .name(name)
                .sortOrder(sortOrder)
                .statistics(statistics)
                .children(children)
                .build();

        assertNotNull(rsp);
        assertEquals(id, rsp.getId());
        assertEquals(EntityType.DEPARTMENT, rsp.getType());
        assertEquals(name, rsp.getName());
        assertEquals(sortOrder, rsp.getSortOrder());
        assertEquals(statistics, rsp.getStatistics());
        assertEquals(children, rsp.getChildren());
    }

    @Test
    @DisplayName("TreeNodeRsp - Test constructor")
    void testTreeNodeRspConstructor() {
        UUID id = UUID.randomUUID();
        String name = "Test Node";
        Integer sortOrder = 1;
        TreeStatistics statistics = new TreeStatistics(2, 3, 5);
        List<TreeNodeRsp> children = List.of();

        TreeNodeRsp rsp = new TreeNodeRsp(id, EntityType.GROUP, name, sortOrder, statistics, children);

        assertNotNull(rsp);
        assertEquals(id, rsp.getId());
        assertEquals(EntityType.GROUP, rsp.getType());
        assertEquals(name, rsp.getName());
        assertEquals(sortOrder, rsp.getSortOrder());
        assertEquals(statistics, rsp.getStatistics());
        assertEquals(children, rsp.getChildren());
    }

    @Test
    @DisplayName("TreeNodeRsp - Test with nested children")
    void testTreeNodeRspWithNestedChildren() {
        UUID rootId = UUID.randomUUID();
        UUID childId1 = UUID.randomUUID();
        UUID childId2 = UUID.randomUUID();

        TreeStatistics rootStats = new TreeStatistics(1, 2, 3);
        TreeStatistics childStats1 = new TreeStatistics(0, 1, 2);
        TreeStatistics childStats2 = new TreeStatistics(0, 0, 1);

        TreeNodeRsp child1 = new TreeNodeRsp(childId1, EntityType.DEPARTMENT, "Child 1", 1, childStats1, List.of());
        TreeNodeRsp child2 = new TreeNodeRsp(childId2, EntityType.PERSONNEL, "Child 2", 2, childStats2, List.of());

        TreeNodeRsp root = new TreeNodeRsp(rootId, EntityType.ROOT, "Root", 0, rootStats, List.of(child1, child2));

        assertNotNull(root);
        assertEquals(2, root.getChildren().size());
        assertEquals(EntityType.ROOT, root.getType());
        assertEquals("Child 1", root.getChildren().get(0).getName());
        assertEquals("Child 2", root.getChildren().get(1).getName());
    }

    @Test
    @DisplayName("TreeStatistics - Test constructor")
    void testTreeStatisticsConstructor() {
        Integer subGroupCount = 5;
        Integer subDepartmentCount = 10;
        Integer personnelCount = 20;

        TreeStatistics statistics = new TreeStatistics(subGroupCount, subDepartmentCount, personnelCount);

        assertNotNull(statistics);
        assertEquals(subGroupCount, statistics.getSubGroupCount());
        assertEquals(subDepartmentCount, statistics.getSubDepartmentCount());
        assertEquals(personnelCount, statistics.getPersonnelCount());
    }

    @Test
    @DisplayName("TreeStatistics - Test builder pattern")
    void testTreeStatisticsBuilder() {
        TreeStatistics statistics = TreeStatistics.builder()
                .subGroupCount(3)
                .subDepartmentCount(6)
                .personnelCount(12)
                .build();

        assertNotNull(statistics);
        assertEquals(3, statistics.getSubGroupCount());
        assertEquals(6, statistics.getSubDepartmentCount());
        assertEquals(12, statistics.getPersonnelCount());
    }

    @Test
    @DisplayName("TreeStatistics - Test default constructor")
    void testTreeStatisticsDefaultConstructor() {
        TreeStatistics statistics = new TreeStatistics();

        assertNotNull(statistics);
        assertNull(statistics.getSubGroupCount());
        assertNull(statistics.getSubDepartmentCount());
        assertNull(statistics.getPersonnelCount());
    }

    @Test
    @DisplayName("EntityType enum values")
    void testEntityTypeValues() {
        EntityType[] values = EntityType.values();

        assertEquals(4, values.length);
        assertEquals(EntityType.ROOT, values[0]);
        assertEquals(EntityType.GROUP, values[1]);
        assertEquals(EntityType.DEPARTMENT, values[2]);
        assertEquals(EntityType.PERSONNEL, values[3]);
    }
}
