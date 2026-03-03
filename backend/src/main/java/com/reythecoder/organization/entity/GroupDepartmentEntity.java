package com.reythecoder.organization.entity;

import io.github.robsonkades.uuidv7.UUIDv7;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "org_group_department", uniqueConstraints = @UniqueConstraint(columnNames = {"group_id", "department_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupDepartmentEntity {

    @Id
    private UUID id;

    @Column(name = "group_id", nullable = false)
    private UUID groupId;

    @Column(name = "department_id", nullable = false)
    private UUID departmentId;

    @Column(name = "role", length = 100, nullable = false)
    private String role;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "create_time", nullable = false)
    private OffsetDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private OffsetDateTime updateTime;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    public GroupDepartmentEntity(UUID groupId, UUID departmentId, String role, Integer sortOrder) {
        this.id = UUIDv7.randomUUID();
        this.groupId = groupId;
        this.departmentId = departmentId;
        this.role = role != null ? role : "";
        this.sortOrder = sortOrder != null ? sortOrder : 0;
        this.createTime = OffsetDateTime.now();
        this.updateTime = OffsetDateTime.now();
        this.tenantId = UUID.fromString("00000000-0000-0000-0000-000000000000");
    }
}