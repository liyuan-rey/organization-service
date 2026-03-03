package com.reythecoder.organization.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupRsp {
    
    private UUID id;
    
    private String name;
    
    private String description;
    
    private OffsetDateTime createTime;
    
    private OffsetDateTime updateTime;
}