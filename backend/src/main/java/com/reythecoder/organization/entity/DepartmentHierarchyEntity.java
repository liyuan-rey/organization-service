package com.reythecoder.organization.entity;

import io.github.robsonkades.uuidv7.UUIDv7;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "org_department_hierarchy", uniqueConstraints = @UniqueConstraint(columnNames = {"child_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentHierarchyEntity {

    @Id
    private UUID id;

    @Column(name = "parent_id")
    private UUID parentId;

    @Column(name = "child_id", nullable = false)
    private UUID childId;

    @Column(name = "level", nullable = false)
    private Integer level;

    @Column(name = "path", length = 1000, nullable = false)
    private String path;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "create_time", nullable = false)
    private OffsetDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private OffsetDateTime updateTime;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    public DepartmentHierarchyEntity(UUID parentId, UUID childId, Integer level, String path, Integer sortOrder) {
        this.id = UUIDv7.randomUUID();
        this.parentId = parentId;
        this.childId = childId;
        this.level = level != null ? level : 1;
        this.path = path != null ? path : "";
        this.sortOrder = sortOrder != null ? sortOrder : 0;
        this.createTime = OffsetDateTime.now();
        this.updateTime = OffsetDateTime.now();
        this.tenantId = UUID.fromString("00000000-0000-0000-0000-000000000000");
    }
}