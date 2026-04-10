package com.reythecoder.organization.repository;

import com.reythecoder.organization.entity.EntityType;
import com.reythecoder.organization.entity.OrgTreeNodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for organization tree node operations.
 */
@Repository
public interface OrgTreeNodeRepository extends JpaRepository<OrgTreeNodeEntity, UUID> {

    // =====================================================
    // Standard query methods (method naming convention)
    // =====================================================

    /**
     * Find all child nodes by parent ID, ordered by sort rank.
     *
     * @param parentId the parent node ID
     * @return list of child nodes ordered by sort rank
     */
    List<OrgTreeNodeEntity> findByParentIdOrderBySortRankAsc(UUID parentId);

    /**
     * Find node by entity type and entity ID.
     *
     * @param entityType the entity type
     * @param entityId the entity ID
     * @return the node if found
     */
    Optional<OrgTreeNodeEntity> findByEntityTypeAndEntityId(EntityType entityType, UUID entityId);

    /**
     * Find nodes by entity type and entity ID.
     *
     * @param entityType the entity type
     * @param entityId the entity ID
     * @return list of nodes
     */
    List<OrgTreeNodeEntity> findAllByEntityTypeAndEntityId(EntityType entityType, UUID entityId);

    /**
     * Count child nodes by parent ID.
     *
     * @param parentId the parent node ID
     * @return the count of child nodes
     */
    long countByParentId(UUID parentId);

    /**
     * Find node by parent ID, entity type and entity ID.
     *
     * @param parentId the parent node ID
     * @param entityType the entity type
     * @param entityId the entity ID
     * @return the node if found
     */
    Optional<OrgTreeNodeEntity> findByParentIdAndEntityTypeAndEntityId(UUID parentId, EntityType entityType, UUID entityId);

    /**
     * Count children by type for a given parent.
     *
     * @param parentId the parent node ID
     * @param entityType the entity type to count
     * @return the count of children with the specified type
     */
    long countByParentIdAndEntityType(UUID parentId, EntityType entityType);

    // =====================================================
    // JPQL queries
    // =====================================================

    /**
     * Find all nodes by entity type using JPQL.
     *
     * @param entityType the entity type
     * @return list of nodes with the specified entity type
     */
    @Query("SELECT n FROM OrgTreeNodeEntity n WHERE n.entityType = :entityType")
    List<OrgTreeNodeEntity> findByEntityType(@Param("entityType") EntityType entityType);

    /**
     * Find all nodes at a specific level using JPQL.
     *
     * @param level the hierarchy level
     * @return list of nodes at the specified level
     */
    @Query("SELECT n FROM OrgTreeNodeEntity n WHERE n.level = :level")
    List<OrgTreeNodeEntity> findByLevel(@Param("level") Integer level);

    // =====================================================
    // Native SQL queries (array operations)
    // =====================================================

    /**
     * Find all descendant nodes using PostgreSQL array contains operator.
     *
     * @param nodeId the node ID to find descendants for
     * @return list of descendant nodes
     */
    @Query(value = "SELECT * FROM org_tree WHERE :nodeId = ANY(path)", nativeQuery = true)
    List<OrgTreeNodeEntity> findAllDescendants(@Param("nodeId") UUID nodeId);

    /**
     * Find direct children by parent ID using native SQL.
     *
     * @param parentId the parent node ID
     * @return list of child nodes
     */
    @Query(value = "SELECT * FROM org_tree WHERE parent_id = :parentId ORDER BY sort_rank ASC", nativeQuery = true)
    List<OrgTreeNodeEntity> findChildrenByParentId(@Param("parentId") UUID parentId);

    /**
     * Find the root node (level = 0) using native SQL.
     *
     * @return the root node if found
     */
    @Query(value = "SELECT * FROM org_tree WHERE level = 0 LIMIT 1", nativeQuery = true)
    Optional<OrgTreeNodeEntity> findRootNode();

    /**
     * Find children by multiple parent IDs using native SQL.
     *
     * @param parentIds the list of parent node IDs
     * @return list of child nodes
     */
    @Query(value = "SELECT * FROM org_tree WHERE parent_id IN (:parentIds) ORDER BY parent_id, sort_rank ASC", nativeQuery = true)
    List<OrgTreeNodeEntity> findChildrenByParentIds(@Param("parentIds") List<UUID> parentIds);

    // =====================================================
    // Statistics queries
    // =====================================================

    /**
     * Count children by parent node ID.
     *
     * @param parentId the parent node ID
     * @return the count of child nodes
     */
    @Query(value = "SELECT COUNT(*) FROM org_tree WHERE parent_id = :parentId", nativeQuery = true)
    long countChildrenByType(@Param("parentId") UUID parentId);

    /**
     * Count nodes by entity type and entity ID.
     *
     * @param entityType the entity type
     * @param entityId the entity ID
     * @return the count of matching nodes
     */
    @Query(value = "SELECT COUNT(*) FROM org_tree WHERE entity_type = :entityType AND entity_id = :entityId", nativeQuery = true)
    long countByEntityTypeAndEntityId(@Param("entityType") String entityType, @Param("entityId") UUID entityId);
}
