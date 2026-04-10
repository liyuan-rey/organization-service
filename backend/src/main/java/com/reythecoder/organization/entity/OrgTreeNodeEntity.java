package com.reythecoder.organization.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Organization tree node entity.
 *
 * This entity represents a node in the organization tree structure,
 * supporting Group, Department, and Personnel hierarchy management.
 */
@Entity
@Table(name = "org_tree",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_org_tree_parent_entity",
           columnNames = {"parent_id", "entity_type", "entity_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrgTreeNodeEntity {

    @Id
    private UUID id;

    @Column(name = "parent_id", nullable = false)
    private UUID parentId;

    @Column(name = "entity_type", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private EntityType entityType;

    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    @Column(name = "alias", length = 100, nullable = false)
    private String alias;

    @Column(name = "level", nullable = false)
    private Integer level;

    @Column(name = "path", nullable = false, columnDefinition = "UUID[]")
    private UUID[] path;

    @Column(name = "sort_rank", length = 12, nullable = false)
    private String sortRank;

    @Column(name = "create_time", nullable = false)
    private OffsetDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private OffsetDateTime updateTime;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;
}
