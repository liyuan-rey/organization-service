package com.reythecoder.organization.entity;

import io.github.robsonkades.uuidv7.UUIDv7;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "org_department_personnel", uniqueConstraints = @UniqueConstraint(columnNames = {"department_id", "personnel_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentPersonnelEntity {

    @Id
    private UUID id;

    @Column(name = "department_id", nullable = false)
    private UUID departmentId;

    @Column(name = "personnel_id", nullable = false)
    private UUID personnelId;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary;

    @Column(name = "position", length = 100, nullable = false)
    private String position;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "create_time", nullable = false)
    private OffsetDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private OffsetDateTime updateTime;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    public DepartmentPersonnelEntity(UUID departmentId, UUID personnelId, Boolean isPrimary, String position, Integer sortOrder) {
        this.id = UUIDv7.randomUUID();
        this.departmentId = departmentId;
        this.personnelId = personnelId;
        this.isPrimary = isPrimary != null ? isPrimary : false;
        this.position = position != null ? position : "";
        this.sortOrder = sortOrder != null ? sortOrder : 0;
        this.createTime = OffsetDateTime.now();
        this.updateTime = OffsetDateTime.now();
        this.tenantId = UUID.fromString("00000000-0000-0000-0000-000000000000");
    }
}