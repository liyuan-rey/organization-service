package com.reythecoder.organization.dto.response;

import com.reythecoder.organization.entity.EntityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Response DTO for tree node.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreeNodeRsp {

    /**
     * Tree node ID.
     */
    private UUID id;

    /**
     * Entity type (ROOT, GROUP, DEPARTMENT, PERSONNEL).
     */
    private EntityType type;

    /**
     * Node name/alias.
     */
    private String name;

    /**
     * Sort order.
     */
    private Integer sortOrder;

    /**
     * Tree statistics for this node.
     */
    private TreeStatistics statistics;

    /**
     * Children nodes.
     */
    private List<TreeNodeRsp> children;
}