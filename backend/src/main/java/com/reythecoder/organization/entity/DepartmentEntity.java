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
@Table(name = "org_department")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentEntity {

    @Id
    //@JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "english_name", length = 255, nullable = false)
    private String englishName;

    @Column(name = "short_name", length = 100, nullable = false)
    private String shortName;

    @Column(name = "org_code", length = 50, nullable = false)
    private String orgCode;

    @Column(name = "phone", length = 50, nullable = false)
    private String phone;

    @Column(name = "fax", length = 50, nullable = false)
    private String fax;

    @Column(name = "email", length = 100, nullable = false)
    private String email;

    @Column(name = "address", length = 500, nullable = false)
    private String address;

    @Column(name = "postal_code", length = 20, nullable = false)
    private String postalCode;

    @Column(name = "create_time", nullable = false)
    private OffsetDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private OffsetDateTime updateTime;

    @Column(name = "tenant_id", nullable = false)
    //@JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID tenantId;

    @Column(name = "removed", nullable = false)
    private boolean removed = false;

    // JPA requires a no-args constructor, which is provided by @NoArgsConstructor

    // Custom constructor for creating new entities with default values
    public DepartmentEntity(String name, String englishName, String shortName, String orgCode,
                          String phone, String fax, String email, String address, String postalCode) {
        this.id = UUIDv7.randomUUID();
        this.name = name;
        this.englishName = englishName;
        this.shortName = shortName;
        this.orgCode = orgCode;
        this.phone = phone;
        this.fax = fax;
        this.email = email;
        this.address = address;
        this.postalCode = postalCode;
        this.createTime = OffsetDateTime.now();
        this.updateTime = OffsetDateTime.now();
        this.tenantId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        this.removed = false;
    }
}