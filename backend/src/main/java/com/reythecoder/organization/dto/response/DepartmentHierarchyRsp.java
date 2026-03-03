package com.reythecoder.organization.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentHierarchyRsp {
    
    private UUID id;
    
    private UUID parentId;
    
    private UUID childId;
    
    private String childName;
    
    private Integer level;
    
    private String path;
    
    private Integer sortOrder;
    
    private OffsetDateTime createTime;
    
    private OffsetDateTime updateTime;
}