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
    // 默认构造函数，用于JPA
    public DepartmentEntity {
        if (id == null) {
            id = UUIDv7.randomUUID();
        }
        if (createTime == null) {
            createTime = OffsetDateTime.now();
        }
        if (updateTime == null) {
            updateTime = OffsetDateTime.now();
        }
        if (tenantId == null) {
            tenantId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        }
    }
}