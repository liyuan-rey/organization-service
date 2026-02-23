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
@Table(name = "org_personnel")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonnelEntity {

    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "gender", length = 1, nullable = false)
    private String gender;

    @Column(name = "id_card", length = 18, nullable = false)
    private String idCard;

    @Column(name = "mobile", length = 20, nullable = false)
    private String mobile;

    @Column(name = "telephone", length = 20, nullable = false)
    private String telephone;

    @Column(name = "fax", length = 20, nullable = false)
    private String fax;

    @Column(name = "email", length = 100, nullable = false)
    private String email;

    @Column(name = "photo")
    private byte[] photo;

    @Column(name = "create_time", nullable = false)
    private OffsetDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private OffsetDateTime updateTime;

    @Column(name = "tenant_id", nullable = false)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID tenantId;

    // Custom constructor for creating new personnel with default values
    public PersonnelEntity(String name, String gender, String idCard, String mobile,
                          String telephone, String fax, String email, byte[] photo) {
        this.id = UUIDv7.randomUUID();
        this.name = name;
        this.gender = gender;
        this.idCard = idCard;
        this.mobile = mobile;
        this.telephone = telephone;
        this.fax = fax;
        this.email = email;
        this.photo = photo;
        this.createTime = OffsetDateTime.now();
        this.updateTime = OffsetDateTime.now();
        this.tenantId = UUID.fromString("00000000-0000-0000-0000-000000000000");
    }
}