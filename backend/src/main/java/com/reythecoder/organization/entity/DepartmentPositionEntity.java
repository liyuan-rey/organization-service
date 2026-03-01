package com.reythecoder.organization.entity;

import io.github.robsonkades.uuidv7.UUIDv7;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "org_department_position", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"department_id", "position_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentPositionEntity {

    @Id
    //@JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(name = "department_id", nullable = false)
    //@JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID departmentId;

    @Column(name = "position_id", nullable = false)
    //@JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID positionId;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "create_time", nullable = false)
    private OffsetDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private OffsetDateTime updateTime;

    @Column(name = "tenant_id", nullable = false)
    //@JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID tenantId;

    // Custom constructor
    public DepartmentPositionEntity(UUID departmentId, UUID positionId, Boolean isPrimary, Integer sortOrder) {
        this.id = UUIDv7.randomUUID();
        this.departmentId = departmentId;
        this.positionId = positionId;
        this.isPrimary = isPrimary != null ? isPrimary : false;
        this.sortOrder = sortOrder != null ? sortOrder : 0;
        this.createTime = OffsetDateTime.now();
        this.updateTime = OffsetDateTime.now();
        this.tenantId = UUID.fromString("00000000-0000-0000-0000-000000000000");
    }
}
