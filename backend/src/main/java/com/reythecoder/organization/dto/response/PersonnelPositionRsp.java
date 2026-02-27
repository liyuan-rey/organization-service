package com.reythecoder.organization.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonnelPositionRsp {
    private UUID id;
    private UUID personnelId;
    private String personnelName;
    private UUID positionId;
    private String positionName;
    private UUID departmentId;
    private String departmentName;
    private Boolean isPrimary;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer status;
    private OffsetDateTime createTime;
    private OffsetDateTime updateTime;
}
