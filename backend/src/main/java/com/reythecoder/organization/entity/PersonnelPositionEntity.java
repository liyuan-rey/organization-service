package com.reythecoder.organization.entity;

import io.github.robsonkades.uuidv7.UUIDv7;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "org_personnel_position",
       uniqueConstraints = @UniqueConstraint(columnNames = {"personnel_id", "position_id", "department_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonnelPositionEntity {

    @Id
    //@JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(name = "personnel_id", nullable = false)
    //@JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID personnelId;

    @Column(name = "position_id", nullable = false)
    //@JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID positionId;

    @Column(name = "department_id")
    //@JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID departmentId;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "create_time", nullable = false)
    private OffsetDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private OffsetDateTime updateTime;

    @Column(name = "tenant_id", nullable = false)
    //@JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID tenantId;

    // Custom constructor
    public PersonnelPositionEntity(UUID personnelId, UUID positionId, UUID departmentId,
                                   Boolean isPrimary, LocalDate startDate, LocalDate endDate, Integer status) {
        this.id = UUIDv7.randomUUID();
        this.personnelId = personnelId;
        this.positionId = positionId;
        this.departmentId = departmentId;
        this.isPrimary = isPrimary != null ? isPrimary : false;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status != null ? status : 1;
        this.createTime = OffsetDateTime.now();
        this.updateTime = OffsetDateTime.now();
        this.tenantId = UUID.fromString("00000000-0000-0000-0000-000000000000");
    }
}
