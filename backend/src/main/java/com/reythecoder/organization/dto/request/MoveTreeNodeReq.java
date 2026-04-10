package com.reythecoder.organization.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for moving a tree node to a new parent.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoveTreeNodeReq {

    /**
     * Node ID to move.
     */
    @NotNull(message = "节点 ID 不能为空")
    private UUID nodeId;

    /**
     * New parent node ID.
     */
    @NotNull(message = "新父节点 ID 不能为空")
    private UUID newParentId;

    /**
     * New sort rank in the target parent (LexoRank format).
     */
    @NotNull(message = "新排序值不能为空")
    @Size(max = 12, message = "排序值长度不能超过 12 个字符")
    private String newSortRank;
}
