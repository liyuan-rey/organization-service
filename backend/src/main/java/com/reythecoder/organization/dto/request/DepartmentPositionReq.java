package com.reythecoder.organization.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentPositionReq {
    
    @NotNull(message = "部门 ID 不能为空")
    private UUID departmentId;
    
    @NotNull(message = "岗位 ID 不能为空")
    private UUID positionId;
    
    private Boolean isPrimary;
    
    private Integer sortOrder;
}
