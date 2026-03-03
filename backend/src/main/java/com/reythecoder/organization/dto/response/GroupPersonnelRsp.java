package com.reythecoder.organization.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupPersonnelRsp {
    
    private UUID id;
    
    private UUID groupId;
    
    private String groupName;
    
    private UUID personnelId;
    
    private String personnelName;
    
    private String role;
    
    private Integer sortOrder;
    
    private OffsetDateTime createTime;
    
    private OffsetDateTime updateTime;
}