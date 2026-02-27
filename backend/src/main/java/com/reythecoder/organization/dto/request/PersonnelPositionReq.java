package com.reythecoder.organization.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonnelPositionReq {
    
    @NotNull(message = "人员 ID 不能为空")
    private UUID personnelId;
    
    @NotNull(message = "岗位 ID 不能为空")
    private UUID positionId;
    
    private UUID departmentId;
    
    private Boolean isPrimary;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private Integer status;
}
