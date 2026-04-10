package com.reythecoder.organization.service;

import com.reythecoder.organization.dto.response.TreeNodeRsp;
import com.reythecoder.organization.entity.EntityType;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for organization tree node operations.
 */
public interface OrgTreeNodeService {

    // =====================================================
    // CRUD operations
    // =====================================================

    /**
     * Create a new tree node under the specified parent.
     *
     * @param parentId the parent node ID (null for root node)
     * @param entityType the entity type (GROUP, DEPARTMENT, PERSONNEL)
     * @param entityId the entity ID
     * @param alias the node alias (display name)
     * @return the created tree node
     */
    TreeNodeRsp createNode(UUID parentId, EntityType entityType, UUID entityId, String alias);

    /**
     * Create a new tree node after a specific sibling node.
     *
     * @param parentId the parent node ID
     * @param entityType the entity type
     * @param entityId the entity ID
     * @param alias the node alias
     * @param afterNodeId the sibling node ID to insert after
     * @return the created tree node
     */
    TreeNodeRsp createNodeAfter(UUID parentId, EntityType entityType, UUID entityId, String alias, UUID afterNodeId);

    /**
     * Update an existing tree node.
     *
     * @param nodeId the node ID to update
     * @param alias the new alias (null to keep unchanged)
     * @param sortRank the new sort rank (null to keep unchanged)
     * @return the updated tree node
     */
    TreeNodeRsp updateNode(UUID nodeId, String alias, String sortRank);

    /**
     * Move a tree node to a new parent.
     *
     * @param nodeId the node ID to move
     * @param newParentId the new parent node ID
     * @return the moved tree node
     */
    TreeNodeRsp moveNode(UUID nodeId, UUID newParentId);

    /**
     * Move a tree node after a specific sibling node.
     *
     * @param nodeId the node ID to move
     * @param newParentId the new parent node ID
     * @param afterNodeId the sibling node ID to insert after
     * @return the moved tree node
     */
    TreeNodeRsp moveNodeAfter(UUID nodeId, UUID newParentId, UUID afterNodeId);

    /**
     * Remove a tree node and all its descendants.
     *
     * @param nodeId the node ID to remove
     */
    void removeNode(UUID nodeId);

    // =====================================================
    // Query operations
    // =====================================================

    /**
     * Get a single tree node by ID.
     *
     * @param nodeId the node ID
     * @return the tree node
     */
    TreeNodeRsp getNode(UUID nodeId);

    /**
     * Get all direct children of a parent node.
     *
     * @param parentId the parent node ID
     * @return list of child nodes
     */
    List<TreeNodeRsp> getChildren(UUID parentId);

    /**
     * Get a subtree starting from the specified node.
     *
     * @param nodeId the root node ID of the subtree
     * @param depth the maximum depth to load (-1 for unlimited)
     * @return the subtree
     */
    TreeNodeRsp getSubTree(UUID nodeId, Integer depth);

    /**
     * Get all descendant nodes of a given node.
     *
     * @param nodeId the node ID
     * @return list of all descendant nodes
     */
    List<TreeNodeRsp> getAllDescendants(UUID nodeId);

    /**
     * Get all ancestor nodes of a given node.
     *
     * @param nodeId the node ID
     * @return list of all ancestor nodes (from root to parent)
     */
    List<TreeNodeRsp> getAllAncestors(UUID nodeId);

    /**
     * Get nodes by entity type and entity ID.
     *
     * @param entityType the entity type
     * @param entityId the entity ID
     * @return list of matching nodes
     */
    List<TreeNodeRsp> getNodesByEntity(EntityType entityType, UUID entityId);

    /**
     * Count the number of direct children of a parent node.
     *
     * @param parentId the parent node ID
     * @return the count of children
     */
    long countChildren(UUID parentId);
}
