package com.reythecoder.organization.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for updating a tree node.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTreeNodeReq {

    /**
     * Node ID to update.
     */
    @NotNull(message = "节点 ID 不能为空")
    private UUID id;

    /**
     * New alias (display name in the tree).
     */
    @Size(max = 100, message = "节点别名长度不能超过 100 个字符")
    private String alias;

    /**
     * New sort rank for ordering (LexoRank format).
     */
    @Size(max = 12, message = "排序值长度不能超过 12 个字符")
    private String sortRank;
}
