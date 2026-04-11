package com.reythecoder.organization.service.impl;

import com.reythecoder.organization.dto.response.TreeNodeRsp;
import com.reythecoder.organization.dto.response.TreeStatistics;
import com.reythecoder.organization.entity.EntityType;
import com.reythecoder.organization.entity.OrgTreeNodeEntity;
import com.reythecoder.common.exception.ApiException;
import com.reythecoder.organization.mapper.OrgTreeNodeMapper;
import com.reythecoder.organization.repository.OrgTreeNodeRepository;
import com.reythecoder.organization.service.OrgTreeNodeService;
import com.reythecoder.common.utils.LexoRankUtils;
import io.github.robsonkades.uuidv7.UUIDv7;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of OrgTreeNodeService.
 */
@Service
@Validated
public class OrgTreeNodeServiceImpl implements OrgTreeNodeService {

    private static final Logger logger = LoggerFactory.getLogger(OrgTreeNodeServiceImpl.class);

    private static final UUID ROOT_TENANT_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    private final OrgTreeNodeRepository repository;
    private final OrgTreeNodeMapper mapper;

    public OrgTreeNodeServiceImpl(OrgTreeNodeRepository repository, OrgTreeNodeMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public TreeNodeRsp createNode(UUID parentId, EntityType entityType, UUID entityId, String alias) {
        logger.info("创建树节点：parentId={}, entityType={}, entityId={}, alias={}", parentId, entityType, entityId, alias);

        // Validate entity type (ROOT cannot be created)
        if (entityType == EntityType.ROOT) {
            throw new ApiException(400, "不能创建 ROOT 类型的节点");
        }

        // Check for duplicate
        if (parentId != null) {
            Optional<OrgTreeNodeEntity> existing = repository.findByParentIdAndEntityTypeAndEntityId(parentId, entityType, entityId);
            if (existing.isPresent()) {
                throw new ApiException(409, "该节点已存在于父节点下");
            }
        }

        // Get parent node to determine level and path
        OrgTreeNodeEntity parentEntity = null;
        int level = 0;
        UUID[] path = new UUID[0];

        if (parentId != null) {
            parentEntity = repository.findById(parentId)
                    .orElseThrow(() -> new ApiException(404, "父节点不存在"));
            level = parentEntity.getLevel() + 1;
            path = concatenateArrays(parentEntity.getPath(), new UUID[]{parentEntity.getId()});
        }

        // Get sort rank for the new node
        List<OrgTreeNodeEntity> siblings = repository.findByParentIdOrderBySortRankAsc(parentId);
        String sortRank;
        if (siblings.isEmpty()) {
            sortRank = LexoRankUtils.initialRank(0);
        } else {
            OrgTreeNodeEntity lastSibling = siblings.get(siblings.size() - 1);
            sortRank = LexoRankUtils.after(lastSibling.getSortRank());
        }

        // Create new node
        OrgTreeNodeEntity newNode = new OrgTreeNodeEntity();
        newNode.setId(UUIDv7.randomUUID());
        newNode.setParentId(parentId);
        newNode.setEntityType(entityType);
        newNode.setEntityId(entityId);
        newNode.setAlias(alias);
        newNode.setLevel(level);
        newNode.setPath(path);
        newNode.setSortRank(sortRank);
        newNode.setCreateTime(OffsetDateTime.now());
        newNode.setUpdateTime(OffsetDateTime.now());
        newNode.setTenantId(ROOT_TENANT_ID);

        OrgTreeNodeEntity savedNode = repository.save(newNode);
        logger.info("树节点创建成功：nodeId={}", savedNode.getId());

        return toTreeNodeRsp(savedNode);
    }

    @Override
    @Transactional
    public TreeNodeRsp createNodeAfter(UUID parentId, EntityType entityType, UUID entityId, String alias, UUID afterNodeId) {
        logger.info("在指定节点后创建树节点：parentId={}, afterNodeId={}, entityType={}, entityId={}, alias={}",
                parentId, afterNodeId, entityType, entityId, alias);

        // Validate entity type (ROOT cannot be created)
        if (entityType == EntityType.ROOT) {
            throw new ApiException(400, "不能创建 ROOT 类型的节点");
        }

        // Check for duplicate
        Optional<OrgTreeNodeEntity> existing = repository.findByParentIdAndEntityTypeAndEntityId(parentId, entityType, entityId);
        if (existing.isPresent()) {
            throw new ApiException(409, "该节点已存在于父节点下");
        }

        // Validate afterNodeId
        OrgTreeNodeEntity afterNode = repository.findById(afterNodeId)
                .orElseThrow(() -> new ApiException(404, "参考节点不存在"));

        if (!Objects.equals(afterNode.getParentId(), parentId)) {
            throw new ApiException(400, "参考节点不属于指定的父节点");
        }

        // Find the next sibling (the one after afterNode)
        List<OrgTreeNodeEntity> siblings = repository.findByParentIdOrderBySortRankAsc(parentId);
        OrgTreeNodeEntity nextSibling = null;
        for (int i = 0; i < siblings.size(); i++) {
            if (siblings.get(i).getId().equals(afterNodeId)) {
                if (i + 1 < siblings.size()) {
                    nextSibling = siblings.get(i + 1);
                }
                break;
            }
        }

        // Calculate sort rank
        String lowerBound = afterNode.getSortRank();
        String upperBound = nextSibling != null ? nextSibling.getSortRank() : null;
        String sortRank = LexoRankUtils.between(lowerBound, upperBound);

        // Get parent node to determine level and path
        OrgTreeNodeEntity parentEntity = null;
        int level = 0;
        UUID[] path = new UUID[0];

        if (parentId != null) {
            parentEntity = repository.findById(parentId)
                    .orElseThrow(() -> new ApiException(404, "父节点不存在"));
            level = parentEntity.getLevel() + 1;
            path = concatenateArrays(parentEntity.getPath(), new UUID[]{parentEntity.getId()});
        }

        // Create new node
        OrgTreeNodeEntity newNode = new OrgTreeNodeEntity();
        newNode.setId(UUIDv7.randomUUID());
        newNode.setParentId(parentId);
        newNode.setEntityType(entityType);
        newNode.setEntityId(entityId);
        newNode.setAlias(alias);
        newNode.setLevel(level);
        newNode.setPath(path);
        newNode.setSortRank(sortRank);
        newNode.setCreateTime(OffsetDateTime.now());
        newNode.setUpdateTime(OffsetDateTime.now());
        newNode.setTenantId(ROOT_TENANT_ID);

        OrgTreeNodeEntity savedNode = repository.save(newNode);
        logger.info("树节点创建成功：nodeId={}", savedNode.getId());

        return toTreeNodeRsp(savedNode);
    }

    @Override
    @Transactional
    public TreeNodeRsp updateNode(UUID nodeId, String alias, String sortRank) {
        logger.info("更新树节点：nodeId={}, alias={}, sortRank={}", nodeId, alias, sortRank);

        OrgTreeNodeEntity node = repository.findById(nodeId)
                .orElseThrow(() -> new ApiException(404, "节点不存在"));

        if (alias != null) {
            node.setAlias(alias);
        }
        if (sortRank != null) {
            node.setSortRank(sortRank);
        }
        node.setUpdateTime(OffsetDateTime.now());

        OrgTreeNodeEntity updatedNode = repository.save(node);
        logger.info("树节点更新成功：nodeId={}", updatedNode.getId());

        return toTreeNodeRsp(updatedNode);
    }

    @Override
    @Transactional
    public TreeNodeRsp moveNode(UUID nodeId, UUID newParentId) {
        logger.info("移动树节点：nodeId={}, newParentId={}", nodeId, newParentId);

        OrgTreeNodeEntity node = repository.findById(nodeId)
                .orElseThrow(() -> new ApiException(404, "节点不存在"));

        // Cannot move root node
        if (node.getLevel() == 0) {
            throw new ApiException(400, "不能移动根节点");
        }

        // Validate new parent
        OrgTreeNodeEntity newParent = null;
        int newLevel = 0;
        UUID[] newPath = new UUID[0];

        if (newParentId != null) {
            newParent = repository.findById(newParentId)
                    .orElseThrow(() -> new ApiException(404, "新父节点不存在"));

            // Prevent moving a node to its own descendant
            if (isDescendant(node.getId(), newParentId)) {
                throw new ApiException(400, "不能将节点移动到其子节点下");
            }

            newLevel = newParent.getLevel() + 1;
            newPath = concatenateArrays(newParent.getPath(), new UUID[]{newParent.getId()});
        }

        // Calculate new sort rank
        List<OrgTreeNodeEntity> newSiblings = repository.findByParentIdOrderBySortRankAsc(newParentId);
        String newSortRank;
        if (newSiblings.isEmpty()) {
            newSortRank = LexoRankUtils.initialRank(0);
        } else {
            OrgTreeNodeEntity lastSibling = newSiblings.get(newSiblings.size() - 1);
            newSortRank = LexoRankUtils.after(lastSibling.getSortRank());
        }

        // Update node
        node.setParentId(newParentId);
        node.setLevel(newLevel);
        node.setPath(newPath);
        node.setSortRank(newSortRank);
        node.setUpdateTime(OffsetDateTime.now());

        OrgTreeNodeEntity movedNode = repository.save(node);
        logger.info("树节点移动成功：nodeId={}", movedNode.getId());

        return toTreeNodeRsp(movedNode);
    }

    @Override
    @Transactional
    public TreeNodeRsp moveNodeAfter(UUID nodeId, UUID newParentId, UUID afterNodeId) {
        logger.info("移动树节点到指定位置：nodeId={}, newParentId={}, afterNodeId={}", nodeId, newParentId, afterNodeId);

        OrgTreeNodeEntity node = repository.findById(nodeId)
                .orElseThrow(() -> new ApiException(404, "节点不存在"));

        // Cannot move root node
        if (node.getLevel() == 0) {
            throw new ApiException(400, "不能移动根节点");
        }

        // Validate afterNodeId
        OrgTreeNodeEntity afterNode = repository.findById(afterNodeId)
                .orElseThrow(() -> new ApiException(404, "参考节点不存在"));

        // Validate new parent
        OrgTreeNodeEntity newParent = null;
        if (newParentId != null) {
            newParent = repository.findById(newParentId)
                    .orElseThrow(() -> new ApiException(404, "新父节点不存在"));

            // Prevent moving a node to its own descendant
            if (isDescendant(node.getId(), newParentId)) {
                throw new ApiException(400, "不能将节点移动到其子节点下");
            }
        }

        // Check that afterNode belongs to the new parent
        if (!Objects.equals(afterNode.getParentId(), newParentId)) {
            throw new ApiException(400, "参考节点不属于指定的新父节点");
        }

        // Find the next sibling after afterNode
        List<OrgTreeNodeEntity> siblings = repository.findByParentIdOrderBySortRankAsc(newParentId);
        OrgTreeNodeEntity nextSibling = null;
        for (int i = 0; i < siblings.size(); i++) {
            if (siblings.get(i).getId().equals(afterNodeId)) {
                if (i + 1 < siblings.size()) {
                    nextSibling = siblings.get(i + 1);
                }
                break;
            }
        }

        // Calculate new sort rank
        String lowerBound = afterNode.getSortRank();
        String upperBound = nextSibling != null ? nextSibling.getSortRank() : null;
        String newSortRank = LexoRankUtils.between(lowerBound, upperBound);

        // Update level and path
        int newLevel = 0;
        UUID[] newPath = new UUID[0];

        if (newParentId != null) {
            newLevel = newParent.getLevel() + 1;
            newPath = concatenateArrays(newParent.getPath(), new UUID[]{newParent.getId()});
        }

        // Update node
        node.setParentId(newParentId);
        node.setLevel(newLevel);
        node.setPath(newPath);
        node.setSortRank(newSortRank);
        node.setUpdateTime(OffsetDateTime.now());

        OrgTreeNodeEntity movedNode = repository.save(node);
        logger.info("树节点移动成功：nodeId={}", movedNode.getId());

        return toTreeNodeRsp(movedNode);
    }

    @Override
    @Transactional
    public void removeNode(UUID nodeId) {
        logger.info("删除树节点：nodeId={}", nodeId);

        OrgTreeNodeEntity node = repository.findById(nodeId)
                .orElseThrow(() -> new ApiException(404, "节点不存在"));

        // Cannot delete root node
        if (node.getLevel() == 0) {
            throw new ApiException(400, "不能删除根节点");
        }

        // Delete all descendants first
        List<OrgTreeNodeEntity> descendants = repository.findAllDescendants(nodeId);
        if (!descendants.isEmpty()) {
            repository.deleteAll(descendants);
        }

        // Delete the node itself
        repository.delete(node);
        logger.info("树节点删除成功：nodeId={}", nodeId);
    }

    @Override
    public TreeNodeRsp getNode(UUID nodeId) {
        logger.info("获取树节点：nodeId={}", nodeId);

        OrgTreeNodeEntity node = repository.findById(nodeId)
                .orElseThrow(() -> new ApiException(404, "节点不存在"));

        return toTreeNodeRsp(node);
    }

    @Override
    public List<TreeNodeRsp> getChildren(UUID parentId) {
        logger.info("获取子节点列表：parentId={}", parentId);

        List<OrgTreeNodeEntity> children = repository.findByParentIdOrderBySortRankAsc(parentId);
        return children.stream()
                .map(this::toTreeNodeRsp)
                .collect(Collectors.toList());
    }

    @Override
    public TreeNodeRsp getSubTree(UUID nodeId, Integer depth) {
        logger.info("获取子树：nodeId={}, depth={}", nodeId, depth);

        OrgTreeNodeEntity rootNode = repository.findById(nodeId)
                .orElseThrow(() -> new ApiException(404, "节点不存在"));

        TreeNodeRsp root = toTreeNodeRsp(rootNode);
        root.setChildren(new ArrayList<>());

        int actualDepth = depth == null || depth < 1 ? 1 : depth;
        boolean loadAll = actualDepth == -1;

        if (loadAll || actualDepth > 1) {
            List<OrgTreeNodeEntity> children = repository.findByParentIdOrderBySortRankAsc(nodeId);
            for (OrgTreeNodeEntity child : children) {
                root.getChildren().add(buildSubTree(child, 2, actualDepth, loadAll));
            }
        }

        return root;
    }

    @Override
    public List<TreeNodeRsp> getAllDescendants(UUID nodeId) {
        logger.info("获取所有后代节点：nodeId={}", nodeId);

        // Check if node exists
        repository.findById(nodeId)
                .orElseThrow(() -> new ApiException(404, "节点不存在"));

        List<OrgTreeNodeEntity> descendants = repository.findAllDescendants(nodeId);
        return descendants.stream()
                .map(this::toTreeNodeRsp)
                .collect(Collectors.toList());
    }

    @Override
    public List<TreeNodeRsp> getAllAncestors(UUID nodeId) {
        logger.info("获取所有祖先节点：nodeId={}", nodeId);

        OrgTreeNodeEntity node = repository.findById(nodeId)
                .orElseThrow(() -> new ApiException(404, "节点不存在"));

        List<TreeNodeRsp> ancestors = new ArrayList<>();
        UUID currentParentId = node.getParentId();

        while (currentParentId != null) {
            OrgTreeNodeEntity parentNode = repository.findById(currentParentId)
                    .orElseThrow(() -> new ApiException(404, "祖先节点不存在"));
            ancestors.add(0, toTreeNodeRsp(parentNode));
            currentParentId = parentNode.getParentId();
        }

        return ancestors;
    }

    @Override
    public List<TreeNodeRsp> getNodesByEntity(EntityType entityType, UUID entityId) {
        logger.info("获取实体对应的树节点：entityType={}, entityId={}", entityType, entityId);

        List<OrgTreeNodeEntity> nodes = repository.findAllByEntityTypeAndEntityId(entityType, entityId);
        return nodes.stream()
                .map(this::toTreeNodeRsp)
                .collect(Collectors.toList());
    }

    @Override
    public long countChildren(UUID parentId) {
        logger.info("统计子节点数量：parentId={}", parentId);
        return repository.countByParentId(parentId);
    }

    @Override
    public TreeNodeRsp getRootNode() {
        logger.info("获取根节点");

        // Find the root node (level = 0)
        List<OrgTreeNodeEntity> rootNodes = repository.findByLevel(0);
        if (rootNodes.isEmpty()) {
            throw new ApiException(404, "根节点不存在");
        }

        return toTreeNodeRsp(rootNodes.get(0));
    }

    // =====================================================
    // Helper methods
    // =====================================================

    /**
     * Convert entity to response DTO.
     */
    private TreeNodeRsp toTreeNodeRsp(OrgTreeNodeEntity entity) {
        TreeNodeRsp rsp = new TreeNodeRsp();
        rsp.setId(entity.getId());
        rsp.setType(entity.getEntityType());
        rsp.setName(entity.getAlias());
        rsp.setSortOrder(entity.getLevel());
        rsp.setStatistics(new TreeStatistics(0, 0, 0));
        rsp.setChildren(new ArrayList<>());
        return rsp;
    }

    /**
     * Recursively build subtree.
     */
    private TreeNodeRsp buildSubTree(OrgTreeNodeEntity entity, int currentDepth, int maxDepth, boolean loadAll) {
        TreeNodeRsp node = toTreeNodeRsp(entity);

        if (loadAll || currentDepth < maxDepth) {
            List<OrgTreeNodeEntity> children = repository.findByParentIdOrderBySortRankAsc(entity.getId());
            for (OrgTreeNodeEntity child : children) {
                node.getChildren().add(buildSubTree(child, currentDepth + 1, maxDepth, loadAll));
            }
        }

        return node;
    }

    /**
     * Check if a node is a descendant of another node.
     */
    private boolean isDescendant(UUID ancestorId, UUID potentialDescendantId) {
        // Get all ancestors of potentialDescendantId
        OrgTreeNodeEntity potentialDescendant = repository.findById(potentialDescendantId)
                .orElse(null);

        if (potentialDescendant == null) {
            return false;
        }

        UUID currentParentId = potentialDescendant.getParentId();
        while (currentParentId != null) {
            if (currentParentId.equals(ancestorId)) {
                return true;
            }
            OrgTreeNodeEntity parent = repository.findById(currentParentId).orElse(null);
            if (parent == null) {
                break;
            }
            currentParentId = parent.getParentId();
        }
        return false;
    }

    /**
     * Concatenate two UUID arrays.
     */
    private UUID[] concatenateArrays(UUID[] first, UUID[] second) {
        UUID[] result = new UUID[first.length + second.length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
