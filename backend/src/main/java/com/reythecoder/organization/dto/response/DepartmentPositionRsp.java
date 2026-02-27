package com.reythecoder.organization.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentPositionRsp {
    private UUID id;
    private UUID departmentId;
    private String departmentName;
    private UUID positionId;
    private String positionName;
    private Boolean isPrimary;
    private Integer sortOrder;
    private OffsetDateTime createTime;
    private OffsetDateTime updateTime;
}
