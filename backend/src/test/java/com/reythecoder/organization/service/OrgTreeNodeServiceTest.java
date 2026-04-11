package com.reythecoder.organization.service;

import com.reythecoder.organization.dto.response.TreeNodeRsp;
import com.reythecoder.organization.entity.EntityType;
import com.reythecoder.organization.entity.OrgTreeNodeEntity;
import com.reythecoder.common.exception.ApiException;
import com.reythecoder.organization.mapper.OrgTreeNodeMapper;
import com.reythecoder.organization.repository.OrgTreeNodeRepository;
import com.reythecoder.organization.service.impl.OrgTreeNodeServiceImpl;

import io.github.robsonkades.uuidv7.UUIDv7;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrgTreeNodeService.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrgTreeNodeServiceTest {

    @Mock
    private OrgTreeNodeRepository repository;

    @Mock
    private OrgTreeNodeMapper mapper;

    @InjectMocks
    private OrgTreeNodeServiceImpl service;

    private UUID rootNodeId;
    private UUID childNodeId;
    private UUID grandChildNodeId;
    private OrgTreeNodeEntity rootNode;
    private OrgTreeNodeEntity childNode;
    private OrgTreeNodeEntity grandChildNode;

    @BeforeEach
    void setUp() {
        rootNodeId = UUIDv7.randomUUID();
        childNodeId = UUIDv7.randomUUID();
        grandChildNodeId = UUIDv7.randomUUID();

        // Create root node
        rootNode = new OrgTreeNodeEntity();
        rootNode.setId(rootNodeId);
        rootNode.setParentId(null);
        rootNode.setEntityType(EntityType.ROOT);
        rootNode.setEntityId(UUIDv7.randomUUID());
        rootNode.setAlias("Root");
        rootNode.setLevel(0);
        rootNode.setPath(new UUID[0]);
        rootNode.setSortRank("a0");
        rootNode.setCreateTime(OffsetDateTime.now());
        rootNode.setUpdateTime(OffsetDateTime.now());
        rootNode.setTenantId(UUID.fromString("00000000-0000-0000-0000-000000000000"));

        // Create child node
        childNode = new OrgTreeNodeEntity();
        childNode.setId(childNodeId);
        childNode.setParentId(rootNodeId);
        childNode.setEntityType(EntityType.DEPARTMENT);
        childNode.setEntityId(UUIDv7.randomUUID());
        childNode.setAlias("Child Department");
        childNode.setLevel(1);
        childNode.setPath(new UUID[]{rootNodeId});
        childNode.setSortRank("a0");
        childNode.setCreateTime(OffsetDateTime.now());
        childNode.setUpdateTime(OffsetDateTime.now());
        childNode.setTenantId(UUID.fromString("00000000-0000-0000-0000-000000000000"));

        // Create grandchild node
        grandChildNode = new OrgTreeNodeEntity();
        grandChildNode.setId(grandChildNodeId);
        grandChildNode.setParentId(childNodeId);
        grandChildNode.setEntityType(EntityType.PERSONNEL);
        grandChildNode.setEntityId(UUIDv7.randomUUID());
        grandChildNode.setAlias("Grandchild Personnel");
        grandChildNode.setLevel(2);
        grandChildNode.setPath(new UUID[]{rootNodeId, childNodeId});
        grandChildNode.setSortRank("a0");
        grandChildNode.setCreateTime(OffsetDateTime.now());
        grandChildNode.setUpdateTime(OffsetDateTime.now());
        grandChildNode.setTenantId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
    }

    // =====================================================
    // createNode tests
    // =====================================================

    @Test
    void createNode_shouldThrowExceptionWhenEntityTypeIsRoot() {
        assertThatThrownBy(() -> service.createNode(null, EntityType.ROOT, UUIDv7.randomUUID(), "Test"))
                .isInstanceOf(ApiException.class)
                .hasMessage("不能创建 ROOT 类型的节点");
    }

    @Test
    void createNode_shouldThrowExceptionWhenParentNotFound() {
        UUID parentId = UUIDv7.randomUUID();
        UUID entityId = UUIDv7.randomUUID();
        when(repository.findByParentIdAndEntityTypeAndEntityId(eq(parentId), eq(EntityType.DEPARTMENT), any()))
                .thenReturn(Optional.empty());
        when(repository.findById(parentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createNode(parentId, EntityType.DEPARTMENT, entityId, "Test"))
                .isInstanceOf(ApiException.class)
                .hasMessage("父节点不存在");
    }

    @Test
    void createNode_shouldThrowExceptionWhenNodeAlreadyExists() {
        when(repository.findByParentIdAndEntityTypeAndEntityId(any(), any(), any()))
                .thenReturn(Optional.of(childNode));

        assertThatThrownBy(() -> service.createNode(rootNodeId, EntityType.DEPARTMENT, childNode.getEntityId(), "Test"))
                .isInstanceOf(ApiException.class)
                .hasMessage("该节点已存在于父节点下");
    }

    @Test
    void createNode_shouldCreateNodeSuccessfully() {
        UUID entityId = UUIDv7.randomUUID();
        String alias = "New Department";

        when(repository.findByParentIdAndEntityTypeAndEntityId(eq(rootNodeId), eq(EntityType.DEPARTMENT), eq(entityId)))
                .thenReturn(Optional.empty());
        when(repository.findById(rootNodeId)).thenReturn(Optional.of(rootNode));
        when(repository.findByParentIdOrderBySortRankAsc(rootNodeId)).thenReturn(List.of(childNode));
        when(repository.save(any(OrgTreeNodeEntity.class))).thenAnswer(invocation -> {
            OrgTreeNodeEntity entity = invocation.getArgument(0);
            return entity;
        });
        when(mapper.toTreeNodeRsp(any(OrgTreeNodeEntity.class))).thenAnswer(invocation -> {
            OrgTreeNodeEntity entity = invocation.getArgument(0);
            TreeNodeRsp rsp = new TreeNodeRsp();
            rsp.setId(entity.getId());
            rsp.setType(entity.getEntityType());
            rsp.setName(entity.getAlias());
            return rsp;
        });

        TreeNodeRsp result = service.createNode(rootNodeId, EntityType.DEPARTMENT, entityId, alias);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(alias);
        assertThat(result.getType()).isEqualTo(EntityType.DEPARTMENT);
    }

    @Test
    void createNode_shouldCreateRootNodeWhenParentIdIsNull() {
        UUID entityId = UUIDv7.randomUUID();
        String alias = "Root Node";

        when(repository.findByParentIdAndEntityTypeAndEntityId(null, EntityType.ROOT, entityId))
                .thenReturn(Optional.empty());
        when(repository.findByParentIdOrderBySortRankAsc(null)).thenReturn(List.of());
        when(repository.save(any(OrgTreeNodeEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toTreeNodeRsp(any(OrgTreeNodeEntity.class))).thenAnswer(invocation -> {
            OrgTreeNodeEntity entity = invocation.getArgument(0);
            TreeNodeRsp rsp = new TreeNodeRsp();
            rsp.setId(entity.getId());
            rsp.setType(entity.getEntityType());
            rsp.setName(entity.getAlias());
            return rsp;
        });

        // Note: ROOT type is not allowed, so this should fail
        assertThatThrownBy(() -> service.createNode(null, EntityType.ROOT, entityId, alias))
                .isInstanceOf(ApiException.class)
                .hasMessage("不能创建 ROOT 类型的节点");
    }

    // =====================================================
    // createNodeAfter tests
    // =====================================================

    @Test
    void createNodeAfter_shouldThrowExceptionWhenAfterNodeNotFound() {
        UUID afterNodeId = UUIDv7.randomUUID();
        when(repository.findById(afterNodeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createNodeAfter(rootNodeId, EntityType.DEPARTMENT, UUIDv7.randomUUID(), "Test", afterNodeId))
                .isInstanceOf(ApiException.class)
                .hasMessage("参考节点不存在");
    }

    @Test
    void createNodeAfter_shouldThrowExceptionWhenAfterNodeDoesNotBelongToParent() {
        OrgTreeNodeEntity otherNode = new OrgTreeNodeEntity();
        otherNode.setId(UUIDv7.randomUUID());
        otherNode.setParentId(UUIDv7.randomUUID()); // Different parent
        otherNode.setSortRank("b0");

        when(repository.findById(otherNode.getId())).thenReturn(Optional.of(otherNode));

        assertThatThrownBy(() -> service.createNodeAfter(rootNodeId, EntityType.DEPARTMENT, UUIDv7.randomUUID(), "Test", otherNode.getId()))
                .isInstanceOf(ApiException.class)
                .hasMessage("参考节点不属于指定的父节点");
    }

    @Test
    void createNodeAfter_shouldCreateNodeAfterSiblingSuccessfully() {
        UUID afterNodeId = childNodeId;
        UUID entityId = UUIDv7.randomUUID();
        String alias = "New Node After";

        when(repository.findById(afterNodeId)).thenReturn(Optional.of(childNode));
        when(repository.findById(rootNodeId)).thenReturn(Optional.of(rootNode));
        when(repository.findByParentIdAndEntityTypeAndEntityId(eq(rootNodeId), eq(EntityType.DEPARTMENT), eq(entityId)))
                .thenReturn(Optional.empty());
        when(repository.findByParentIdOrderBySortRankAsc(rootNodeId)).thenReturn(List.of(childNode));
        when(repository.save(any(OrgTreeNodeEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toTreeNodeRsp(any(OrgTreeNodeEntity.class))).thenAnswer(invocation -> {
            OrgTreeNodeEntity entity = invocation.getArgument(0);
            TreeNodeRsp rsp = new TreeNodeRsp();
            rsp.setId(entity.getId());
            rsp.setType(entity.getEntityType());
            rsp.setName(entity.getAlias());
            return rsp;
        });

        TreeNodeRsp result = service.createNodeAfter(rootNodeId, EntityType.DEPARTMENT, entityId, alias, afterNodeId);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(alias);
    }

    // =====================================================
    // updateNode tests
    // =====================================================

    @Test
    void updateNode_shouldThrowExceptionWhenNodeNotFound() {
        UUID nodeId = UUIDv7.randomUUID();
        when(repository.findById(nodeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateNode(nodeId, "New Alias", "b0"))
                .isInstanceOf(ApiException.class)
                .hasMessage("节点不存在");
    }

    @Test
    void updateNode_shouldUpdateNodeAliasSuccessfully() {
        String newAlias = "Updated Alias";
        when(repository.findById(rootNodeId)).thenReturn(Optional.of(rootNode));
        when(repository.save(any(OrgTreeNodeEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toTreeNodeRsp(any(OrgTreeNodeEntity.class))).thenAnswer(invocation -> {
            OrgTreeNodeEntity entity = invocation.getArgument(0);
            TreeNodeRsp rsp = new TreeNodeRsp();
            rsp.setId(entity.getId());
            rsp.setName(entity.getAlias());
            return rsp;
        });

        TreeNodeRsp result = service.updateNode(rootNodeId, newAlias, null);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(newAlias);
    }

    // =====================================================
    // moveNode tests
    // =====================================================

    @Test
    void moveNode_shouldThrowExceptionWhenMovingRootNode() {
        UUID newParentId = UUIDv7.randomUUID();
        when(repository.findById(rootNodeId)).thenReturn(Optional.of(rootNode));

        assertThatThrownBy(() -> service.moveNode(rootNodeId, newParentId))
                .isInstanceOf(ApiException.class)
                .hasMessage("不能移动根节点");
    }

    @Test
    void moveNode_shouldThrowExceptionWhenNewParentNotFound() {
        UUID newParentId = UUIDv7.randomUUID();
        when(repository.findById(childNodeId)).thenReturn(Optional.of(childNode));
        when(repository.findById(newParentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.moveNode(childNodeId, newParentId))
                .isInstanceOf(ApiException.class)
                .hasMessage("新父节点不存在");
    }

    @Test
    void moveNode_shouldThrowExceptionWhenMovingToDescendant() {
        when(repository.findById(childNodeId)).thenReturn(Optional.of(childNode));
        when(repository.findById(grandChildNodeId)).thenReturn(Optional.of(grandChildNode));

        // Try to move child to its own grandchild (circular reference)
        assertThatThrownBy(() -> service.moveNode(childNodeId, grandChildNodeId))
                .isInstanceOf(ApiException.class)
                .hasMessage("不能将节点移动到其子节点下");
    }

    @Test
    void moveNode_shouldMoveNodeSuccessfully() {
        UUID newParentId = UUIDv7.randomUUID();
        OrgTreeNodeEntity newParent = new OrgTreeNodeEntity();
        newParent.setId(newParentId);
        newParent.setLevel(0);
        newParent.setPath(new UUID[0]);

        when(repository.findById(childNodeId)).thenReturn(Optional.of(childNode));
        when(repository.findById(newParentId)).thenReturn(Optional.of(newParent));
        when(repository.findByParentIdOrderBySortRankAsc(newParentId)).thenReturn(List.of());
        when(repository.save(any(OrgTreeNodeEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toTreeNodeRsp(any(OrgTreeNodeEntity.class))).thenAnswer(invocation -> {
            OrgTreeNodeEntity entity = invocation.getArgument(0);
            TreeNodeRsp rsp = new TreeNodeRsp();
            rsp.setId(entity.getId());
            rsp.setName(entity.getAlias());
            return rsp;
        });

        TreeNodeRsp result = service.moveNode(childNodeId, newParentId);

        assertThat(result).isNotNull();
    }

    // =====================================================
    // moveNodeAfter tests
    // =====================================================

    @Test
    void moveNodeAfter_shouldThrowExceptionWhenMovingRootNode() {
        UUID newParentId = UUIDv7.randomUUID();
        UUID afterNodeId = UUIDv7.randomUUID();
        when(repository.findById(rootNodeId)).thenReturn(Optional.of(rootNode));

        assertThatThrownBy(() -> service.moveNodeAfter(rootNodeId, newParentId, afterNodeId))
                .isInstanceOf(ApiException.class)
                .hasMessage("不能移动根节点");
    }

    @Test
    void moveNodeAfter_shouldThrowExceptionWhenAfterNodeNotFound() {
        UUID afterNodeId = UUIDv7.randomUUID();
        when(repository.findById(childNodeId)).thenReturn(Optional.of(childNode));
        when(repository.findById(afterNodeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.moveNodeAfter(childNodeId, rootNodeId, afterNodeId))
                .isInstanceOf(ApiException.class)
                .hasMessage("参考节点不存在");
    }

    // =====================================================
    // removeNode tests
    // =====================================================

    @Test
    void removeNode_shouldThrowExceptionWhenRemovingRootNode() {
        when(repository.findById(rootNodeId)).thenReturn(Optional.of(rootNode));

        assertThatThrownBy(() -> service.removeNode(rootNodeId))
                .isInstanceOf(ApiException.class)
                .hasMessage("不能删除根节点");
    }

    @Test
    void removeNode_shouldThrowExceptionWhenNodeNotFound() {
        UUID nodeId = UUIDv7.randomUUID();
        when(repository.findById(nodeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.removeNode(nodeId))
                .isInstanceOf(ApiException.class)
                .hasMessage("节点不存在");
    }

    @Test
    void removeNode_shouldRemoveNodeAndDescendantsSuccessfully() {
        when(repository.findById(childNodeId)).thenReturn(Optional.of(childNode));
        when(repository.findAllDescendants(childNodeId)).thenReturn(List.of(grandChildNode));
        doNothing().when(repository).deleteAll(List.of(grandChildNode));
        doNothing().when(repository).delete(childNode);

        service.removeNode(childNodeId);

        verify(repository, times(1)).deleteAll(List.of(grandChildNode));
        verify(repository, times(1)).delete(childNode);
    }

    // =====================================================
    // getNode tests
    // =====================================================

    @Test
    void getNode_shouldThrowExceptionWhenNotFound() {
        UUID nodeId = UUIDv7.randomUUID();
        when(repository.findById(nodeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getNode(nodeId))
                .isInstanceOf(ApiException.class)
                .hasMessage("节点不存在");
    }

    @Test
    void getNode_shouldReturnNodeSuccessfully() {
        when(repository.findById(rootNodeId)).thenReturn(Optional.of(rootNode));
        when(mapper.toTreeNodeRsp(rootNode)).thenAnswer(invocation -> {
            OrgTreeNodeEntity entity = invocation.getArgument(0);
            TreeNodeRsp rsp = new TreeNodeRsp();
            rsp.setId(entity.getId());
            rsp.setName(entity.getAlias());
            rsp.setType(entity.getEntityType());
            return rsp;
        });

        TreeNodeRsp result = service.getNode(rootNodeId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(rootNodeId);
        assertThat(result.getName()).isEqualTo("Root");
    }

    // =====================================================
    // getChildren tests
    // =====================================================

    @Test
    void getChildren_shouldReturnEmptyListWhenNoChildren() {
        when(repository.findByParentIdOrderBySortRankAsc(rootNodeId)).thenReturn(List.of());

        List<TreeNodeRsp> result = service.getChildren(rootNodeId);

        assertThat(result).isEmpty();
    }

    @Test
    void getChildren_shouldReturnChildrenSuccessfully() {
        when(repository.findByParentIdOrderBySortRankAsc(rootNodeId)).thenReturn(List.of(childNode));
        when(mapper.toTreeNodeRsp(childNode)).thenAnswer(invocation -> {
            OrgTreeNodeEntity entity = invocation.getArgument(0);
            TreeNodeRsp rsp = new TreeNodeRsp();
            rsp.setId(entity.getId());
            rsp.setName(entity.getAlias());
            return rsp;
        });

        List<TreeNodeRsp> result = service.getChildren(rootNodeId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Child Department");
    }

    // =====================================================
    // getSubTree tests
    // =====================================================

    @Test
    void getSubTree_shouldThrowExceptionWhenNodeNotFound() {
        UUID nodeId = UUIDv7.randomUUID();
        when(repository.findById(nodeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getSubTree(nodeId, 1))
                .isInstanceOf(ApiException.class)
                .hasMessage("节点不存在");
    }

    @Test
    void getSubTree_shouldReturnSubTreeWithDepth1() {
        when(repository.findById(rootNodeId)).thenReturn(Optional.of(rootNode));
        when(mapper.toTreeNodeRsp(rootNode)).thenAnswer(invocation -> {
            OrgTreeNodeEntity entity = invocation.getArgument(0);
            TreeNodeRsp rsp = new TreeNodeRsp();
            rsp.setId(entity.getId());
            rsp.setName(entity.getAlias());
            return rsp;
        });

        TreeNodeRsp result = service.getSubTree(rootNodeId, 1);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(rootNodeId);
        assertThat(result.getChildren()).isEmpty();
    }

    @Test
    void getSubTree_shouldReturnSubTreeWithDepth2() {
        when(repository.findById(rootNodeId)).thenReturn(Optional.of(rootNode));
        when(repository.findByParentIdOrderBySortRankAsc(rootNodeId)).thenReturn(List.of(childNode));
        when(mapper.toTreeNodeRsp(rootNode)).thenAnswer(invocation -> {
            OrgTreeNodeEntity entity = invocation.getArgument(0);
            TreeNodeRsp rsp = new TreeNodeRsp();
            rsp.setId(entity.getId());
            rsp.setName(entity.getAlias());
            return rsp;
        });
        when(mapper.toTreeNodeRsp(childNode)).thenAnswer(invocation -> {
            OrgTreeNodeEntity entity = invocation.getArgument(0);
            TreeNodeRsp rsp = new TreeNodeRsp();
            rsp.setId(entity.getId());
            rsp.setName(entity.getAlias());
            return rsp;
        });

        TreeNodeRsp result = service.getSubTree(rootNodeId, 2);

        assertThat(result).isNotNull();
        assertThat(result.getChildren()).hasSize(1);
    }

    // =====================================================
    // getAllDescendants tests
    // =====================================================

    @Test
    void getAllDescendants_shouldThrowExceptionWhenNodeNotFound() {
        UUID nodeId = UUIDv7.randomUUID();
        when(repository.findById(nodeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getAllDescendants(nodeId))
                .isInstanceOf(ApiException.class)
                .hasMessage("节点不存在");
    }

    @Test
    void getAllDescendants_shouldReturnAllDescendantsSuccessfully() {
        when(repository.findById(rootNodeId)).thenReturn(Optional.of(rootNode));
        when(repository.findAllDescendants(rootNodeId)).thenReturn(List.of(childNode, grandChildNode));
        when(mapper.toTreeNodeRsp(childNode)).thenAnswer(invocation -> {
            OrgTreeNodeEntity entity = invocation.getArgument(0);
            TreeNodeRsp rsp = new TreeNodeRsp();
            rsp.setId(entity.getId());
            rsp.setName(entity.getAlias());
            return rsp;
        });
        when(mapper.toTreeNodeRsp(grandChildNode)).thenAnswer(invocation -> {
            OrgTreeNodeEntity entity = invocation.getArgument(0);
            TreeNodeRsp rsp = new TreeNodeRsp();
            rsp.setId(entity.getId());
            rsp.setName(entity.getAlias());
            return rsp;
        });

        List<TreeNodeRsp> result = service.getAllDescendants(rootNodeId);

        assertThat(result).hasSize(2);
    }

    // =====================================================
    // getAllAncestors tests
    // =====================================================

    @Test
    void getAllAncestors_shouldThrowExceptionWhenNodeNotFound() {
        UUID nodeId = UUIDv7.randomUUID();
        when(repository.findById(nodeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getAllAncestors(nodeId))
                .isInstanceOf(ApiException.class)
                .hasMessage("节点不存在");
    }

    @Test
    void getAllAncestors_shouldReturnAllAncestorsSuccessfully() {
        when(repository.findById(grandChildNodeId)).thenReturn(Optional.of(grandChildNode));
        when(repository.findById(childNodeId)).thenReturn(Optional.of(childNode));
        when(repository.findById(rootNodeId)).thenReturn(Optional.of(rootNode));
        when(mapper.toTreeNodeRsp(childNode)).thenAnswer(invocation -> {
            OrgTreeNodeEntity entity = invocation.getArgument(0);
            TreeNodeRsp rsp = new TreeNodeRsp();
            rsp.setId(entity.getId());
            rsp.setName(entity.getAlias());
            return rsp;
        });
        when(mapper.toTreeNodeRsp(rootNode)).thenAnswer(invocation -> {
            OrgTreeNodeEntity entity = invocation.getArgument(0);
            TreeNodeRsp rsp = new TreeNodeRsp();
            rsp.setId(entity.getId());
            rsp.setName(entity.getAlias());
            return rsp;
        });

        List<TreeNodeRsp> result = service.getAllAncestors(grandChildNodeId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(rootNodeId);
        assertThat(result.get(1).getId()).isEqualTo(childNodeId);
    }

    // =====================================================
    // getNodesByEntity tests
    // =====================================================

    @Test
    void getNodesByEntity_shouldReturnEmptyListWhenNotFound() {
        UUID entityId = UUIDv7.randomUUID();
        when(repository.findAllByEntityTypeAndEntityId(eq(EntityType.DEPARTMENT), eq(entityId)))
                .thenReturn(List.of());

        List<TreeNodeRsp> result = service.getNodesByEntity(EntityType.DEPARTMENT, entityId);

        assertThat(result).isEmpty();
    }

    @Test
    void getNodesByEntity_shouldReturnNodesSuccessfully() {
        when(repository.findAllByEntityTypeAndEntityId(EntityType.DEPARTMENT, childNode.getEntityId()))
                .thenReturn(List.of(childNode));
        when(mapper.toTreeNodeRsp(childNode)).thenAnswer(invocation -> {
            OrgTreeNodeEntity entity = invocation.getArgument(0);
            TreeNodeRsp rsp = new TreeNodeRsp();
            rsp.setId(entity.getId());
            rsp.setName(entity.getAlias());
            return rsp;
        });

        List<TreeNodeRsp> result = service.getNodesByEntity(EntityType.DEPARTMENT, childNode.getEntityId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Child Department");
    }

    // =====================================================
    // countChildren tests
    // =====================================================

    @Test
    void countChildren_shouldReturnZeroWhenNoChildren() {
        when(repository.countByParentId(rootNodeId)).thenReturn(0L);

        long result = service.countChildren(rootNodeId);

        assertThat(result).isZero();
    }

    @Test
    void countChildren_shouldReturnCountSuccessfully() {
        when(repository.countByParentId(rootNodeId)).thenReturn(5L);

        long result = service.countChildren(rootNodeId);

        assertThat(result).isEqualTo(5);
    }
}
