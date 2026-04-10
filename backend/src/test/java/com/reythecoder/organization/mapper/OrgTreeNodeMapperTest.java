package com.reythecoder.organization.mapper;

import com.reythecoder.organization.dto.response.TreeNodeRsp;
import com.reythecoder.organization.entity.EntityType;
import com.reythecoder.organization.entity.OrgTreeNodeEntity;

import io.github.robsonkades.uuidv7.UUIDv7;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrgTreeNodeMapperTest {

    private OrgTreeNodeMapper orgTreeNodeMapper;
    private UUID nodeId;
    private UUID parentId;
    private UUID entityId;
    private UUID tenantId;
    private OffsetDateTime now;

    @BeforeEach
    void setUp() {
        orgTreeNodeMapper = OrgTreeNodeMapper.INSTANCE;
        nodeId = UUIDv7.randomUUID();
        parentId = UUIDv7.randomUUID();
        entityId = UUIDv7.randomUUID();
        tenantId = UUIDv7.randomUUID();
        now = OffsetDateTime.now();
    }

    @Test
    void toTreeNodeRsp_shouldMapEntityToTreeNodeRsp() {
        // Arrange
        OrgTreeNodeEntity entity = new OrgTreeNodeEntity(
                nodeId,
                parentId,
                EntityType.DEPARTMENT,
                entityId,
                "测试节点",
                2,
                new UUID[]{nodeId},
                "a001",
                now,
                now,
                tenantId);

        // Act
        TreeNodeRsp rsp = orgTreeNodeMapper.toTreeNodeRsp(entity);

        // Assert
        assertThat(rsp).isNotNull();
        // id 映射自 entityId
        assertThat(rsp.getId()).isEqualTo(entityId);
        // type 映射自 entityType
        assertThat(rsp.getType()).isEqualTo(EntityType.DEPARTMENT);
        // name 映射自 alias
        assertThat(rsp.getName()).isEqualTo("测试节点");
        // sortOrder 映射自 level
        assertThat(rsp.getSortOrder()).isEqualTo(2);
        // statistics 和 children 被 ignore，应为 null
        assertThat(rsp.getStatistics()).isNull();
        assertThat(rsp.getChildren()).isNull();
    }

    @Test
    void toTreeNodeRspList_shouldMapEntityListToTreeNodeRspList() {
        // Arrange
        UUID entityId1 = UUIDv7.randomUUID();
        UUID entityId2 = UUIDv7.randomUUID();
        UUID nodeId1 = UUIDv7.randomUUID();
        UUID nodeId2 = UUIDv7.randomUUID();

        OrgTreeNodeEntity entity1 = new OrgTreeNodeEntity(
                nodeId1,
                parentId,
                EntityType.GROUP,
                entityId1,
                "节点 1",
                1,
                new UUID[]{nodeId1},
                "a001",
                now,
                now,
                tenantId);

        OrgTreeNodeEntity entity2 = new OrgTreeNodeEntity(
                nodeId2,
                nodeId1,
                EntityType.DEPARTMENT,
                entityId2,
                "节点 2",
                2,
                new UUID[]{nodeId1, nodeId2},
                "a002",
                now,
                now,
                tenantId);

        List<OrgTreeNodeEntity> entities = List.of(entity1, entity2);

        // Act
        List<TreeNodeRsp> rspList = orgTreeNodeMapper.toTreeNodeRspList(entities);

        // Assert
        assertThat(rspList).hasSize(2);

        TreeNodeRsp rsp1 = rspList.get(0);
        assertThat(rsp1.getId()).isEqualTo(entityId1);
        assertThat(rsp1.getType()).isEqualTo(EntityType.GROUP);
        assertThat(rsp1.getName()).isEqualTo("节点 1");
        assertThat(rsp1.getSortOrder()).isEqualTo(1);
        assertThat(rsp1.getStatistics()).isNull();
        assertThat(rsp1.getChildren()).isNull();

        TreeNodeRsp rsp2 = rspList.get(1);
        assertThat(rsp2.getId()).isEqualTo(entityId2);
        assertThat(rsp2.getType()).isEqualTo(EntityType.DEPARTMENT);
        assertThat(rsp2.getName()).isEqualTo("节点 2");
        assertThat(rsp2.getSortOrder()).isEqualTo(2);
        assertThat(rsp2.getStatistics()).isNull();
        assertThat(rsp2.getChildren()).isNull();
    }

    @Test
    void toTreeNodeRspList_shouldReturnNullWhenInputIsNull() {
        // Act
        List<TreeNodeRsp> rspList = orgTreeNodeMapper.toTreeNodeRspList(null);

        // Assert
        assertThat(rspList).isNull();
    }
}
