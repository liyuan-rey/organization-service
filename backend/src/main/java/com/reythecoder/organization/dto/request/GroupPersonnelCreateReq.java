package com.reythecoder.organization.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupPersonnelCreateReq {
    
    @NotNull(message = "分组ID不能为空")
    private UUID groupId;
    
    @NotNull(message = "人员ID不能为空")
    private UUID personnelId;
    
    private String role;
    
    private Integer sortOrder;
}