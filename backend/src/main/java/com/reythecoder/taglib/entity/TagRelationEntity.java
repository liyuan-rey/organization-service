package com.reythecoder.taglib.entity;

import io.github.robsonkades.uuidv7.UUIDv7;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "taglib_tag_relation", uniqueConstraints = @UniqueConstraint(columnNames = {"object_type", "object_id", "tag_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagRelationEntity {

    @Id
    private UUID id;

    @Column(name = "object_type", length = 20, nullable = false)
    private String objectType;

    @Column(name = "object_id", nullable = false)
    private UUID objectId;

    @Column(name = "tag_id", nullable = false)
    private UUID tagId;

    @Column(name = "create_time", nullable = false)
    private OffsetDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private OffsetDateTime updateTime;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    public TagRelationEntity(String objectType, UUID objectId, UUID tagId) {
        this.id = UUIDv7.randomUUID();
        this.objectType = objectType;
        this.objectId = objectId;
        this.tagId = tagId;
        this.createTime = OffsetDateTime.now();
        this.updateTime = OffsetDateTime.now();
        this.tenantId = UUID.fromString("00000000-0000-0000-0000-000000000000");
    }
}
