package com.reythecoder.organization.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentGroupCreateReq {

    @NotNull(message = "部门ID不能为空")
    private UUID departmentId;

    @NotNull(message = "分组ID不能为空")
    private UUID groupId;

    private Integer sortOrder;
}