package com.reythecoder.organization.dto.response;

import com.reythecoder.organization.dto.NodeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreeNodeRsp {
    private UUID id;
    private NodeType type;
    private String name;
    private Integer sortOrder;
    private TreeStatistics statistics;
    private List<TreeNodeRsp> children;
}