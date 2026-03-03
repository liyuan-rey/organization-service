package com.reythecoder.organization.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentPersonnelRsp {
    
    private UUID id;
    
    private UUID departmentId;
    
    private String departmentName;
    
    private UUID personnelId;
    
    private String personnelName;
    
    private Boolean isPrimary;
    
    private String position;
    
    private Integer sortOrder;
    
    private OffsetDateTime createTime;
    
    private OffsetDateTime updateTime;
}