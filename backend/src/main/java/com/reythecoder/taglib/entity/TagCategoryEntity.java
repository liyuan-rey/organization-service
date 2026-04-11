package com.reythecoder.taglib.entity;

import io.github.robsonkades.uuidv7.UUIDv7;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "taglib_category")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagCategoryEntity {

    @Id
    private UUID id;

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "description", length = 500, nullable = false)
    private String description;

    @Column(name = "sort_rank", length = 12, nullable = false)
    private String sortRank;

    @Column(name = "removed", nullable = false)
    private boolean removed = false;

    @Column(name = "create_time", nullable = false)
    private OffsetDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private OffsetDateTime updateTime;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    public TagCategoryEntity(String name, String description, String sortRank) {
        this.id = UUIDv7.randomUUID();
        this.name = name;
        this.description = description != null ? description : "";
        this.sortRank = sortRank != null ? sortRank : "";
        this.removed = false;
        this.createTime = OffsetDateTime.now();
        this.updateTime = OffsetDateTime.now();
        this.tenantId = UUID.fromString("00000000-0000-0000-0000-000000000000");
    }
}
