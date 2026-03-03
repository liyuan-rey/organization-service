package com.reythecoder.organization.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupHierarchyCreateReq {
    
    private UUID parentId;
    
    @NotNull(message = "子分组ID不能为空")
    private UUID childId;
    
    private Integer level;
    
    private String path;
    
    private Integer sortOrder;
}