package com.reythecoder.organization.entity;

import io.github.robsonkades.uuidv7.UUIDv7;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "org_department")
public record DepartmentEntity(
        @Id @JdbcTypeCode(SqlTypes.VARCHAR) UUID id,

        @Column(name = "name", length = 255, nullable = false) String name,

        @Column(name = "english_name", length = 255, nullable = false) String englishName,

        @Column(name = "short_name", length = 100, nullable = false) String shortName,

        @Column(name = "org_code", length = 50, nullable = false) String orgCode,

        @Column(name = "phone", length = 50, nullable = false) String phone,

        @Column(name = "fax", length = 50, nullable = false) String fax,

        @Column(name = "email", length = 100, nullable = false) String email,

        @Column(name = "address", length = 500, nullable = false) String address,

        @Column(name = "postal_code", length = 20, nullable = false) String postalCode,

        @Column(name = "create_time", nullable = false) OffsetDateTime createTime,

        @Column(name = "update_time", nullable = false) OffsetDateTime updateTime,

        @Column(name = "tenant_id", nullable = false) @JdbcTypeCode(SqlTypes.VARCHAR) UUID tenantId) {

    // JPA requires a no-args constructor for proxying
    public DepartmentEntity {
        // Only set defaults for new entities (when id is null)
        if (id == null) {
            id = UUIDv7.randomUUID();
            createTime = createTime != null ? createTime : OffsetDateTime.now();
            updateTime = updateTime != null ? updateTime : OffsetDateTime.now();
            tenantId = tenantId != null ? tenantId : UUID.fromString("00000000-0000-0000-0000-000000000000");
        }
    }

    // No-args constructor for JPA/Hibernate
    public DepartmentEntity() {
        this(
            null,
            "", "", "", "", "", "", "", "", "",
            OffsetDateTime.now(),
            OffsetDateTime.now(),
            UUID.fromString("00000000-0000-0000-0000-000000000000")
        );
    }
}