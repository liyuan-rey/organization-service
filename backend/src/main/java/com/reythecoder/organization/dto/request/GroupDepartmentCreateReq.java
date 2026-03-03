package com.reythecoder.organization.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupDepartmentCreateReq {
    
    @NotNull(message = "分组ID不能为空")
    private UUID groupId;
    
    @NotNull(message = "部门ID不能为空")
    private UUID departmentId;
    
    private String role;
    
    private Integer sortOrder;
}