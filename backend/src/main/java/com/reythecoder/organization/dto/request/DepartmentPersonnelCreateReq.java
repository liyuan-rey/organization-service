package com.reythecoder.organization.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentPersonnelCreateReq {
    
    @NotNull(message = "部门ID不能为空")
    private UUID departmentId;
    
    @NotNull(message = "人员ID不能为空")
    private UUID personnelId;
    
    private Boolean isPrimary;
    
    private String position;
    
    private Integer sortOrder;
}