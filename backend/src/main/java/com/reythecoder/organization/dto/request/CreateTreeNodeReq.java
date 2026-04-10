package com.reythecoder.organization.dto.request;

import com.reythecoder.organization.entity.EntityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for creating a tree node.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTreeNodeReq {

    /**
     * Parent node ID (use null or root UUID for root level nodes).
     */
    private UUID parentId;

    /**
     * Entity type (GROUP, DEPARTMENT, PERSONNEL).
     */
    @NotNull(message = "实体类型不能为空")
    private EntityType entityType;

    /**
     * Entity ID (the actual group/department/personnel ID).
     */
    @NotNull(message = "实体 ID 不能为空")
    private UUID entityId;

    /**
     * Node alias (display name in the tree).
     */
    @NotBlank(message = "节点别名不能为空")
    @Size(max = 100, message = "节点别名长度不能超过 100 个字符")
    private String alias;

    /**
     * Sort rank for ordering (LexoRank format).
     */
    @NotBlank(message = "排序值不能为空")
    @Size(max = 12, message = "排序值长度不能超过 12 个字符")
    private String sortRank;
}
