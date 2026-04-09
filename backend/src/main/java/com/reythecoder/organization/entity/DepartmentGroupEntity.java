package com.reythecoder.organization.entity;

import io.github.robsonkades.uuidv7.UUIDv7;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "org_department_group",
       uniqueConstraints = @UniqueConstraint(columnNames = {"department_id", "group_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentGroupEntity {

    @Id
    private UUID id;

    @Column(name = "department_id", nullable = false)
    private UUID departmentId;

    @Column(name = "group_id", nullable = false)
    private UUID groupId;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "create_time", nullable = false)
    private OffsetDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private OffsetDateTime updateTime;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    public DepartmentGroupEntity(UUID departmentId, UUID groupId, Integer sortOrder) {
        this.id = UUIDv7.randomUUID();
        this.departmentId = departmentId;
        this.groupId = groupId;
        this.sortOrder = sortOrder != null ? sortOrder : 0;
        this.createTime = OffsetDateTime.now();
        this.updateTime = OffsetDateTime.now();
        this.tenantId = UUID.fromString("00000000-0000-0000-0000-000000000000");
    }
}