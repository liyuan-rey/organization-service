package com.reythecoder.organization.entity;

import io.github.robsonkades.uuidv7.UUIDv7;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "org_position")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PositionEntity {

    @Id
    //@JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "code", length = 50, nullable = false)
    private String code;

    @Column(name = "description", length = 500, nullable = false)
    private String description;

    @Column(name = "job_level", length = 50, nullable = false)
    private String jobLevel;

    @Column(name = "job_category", length = 50, nullable = false)
    private String jobCategory;

    @Column(name = "min_salary", precision = 12, scale = 2)
    private BigDecimal minSalary;

    @Column(name = "max_salary", precision = 12, scale = 2)
    private BigDecimal maxSalary;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "create_time", nullable = false)
    private OffsetDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private OffsetDateTime updateTime;

    @Column(name = "tenant_id", nullable = false)
    //@JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID tenantId;

    // Custom constructor for creating new position with default values
    public PositionEntity(String name, String code, String description, String jobLevel,
                         String jobCategory, BigDecimal minSalary, BigDecimal maxSalary, Integer status) {
        this.id = UUIDv7.randomUUID();
        this.name = name;
        this.code = code;
        this.description = description;
        this.jobLevel = jobLevel;
        this.jobCategory = jobCategory;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
        this.status = status != null ? status : 1;
        this.createTime = OffsetDateTime.now();
        this.updateTime = OffsetDateTime.now();
        this.tenantId = UUID.fromString("00000000-0000-0000-0000-000000000000");
    }
}
