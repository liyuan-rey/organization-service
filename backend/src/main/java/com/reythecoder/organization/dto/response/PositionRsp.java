package com.reythecoder.organization.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PositionRsp {
    private UUID id;
    private String name;
    private String code;
    private String description;
    private String jobLevel;
    private String jobCategory;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private Integer status;
    private OffsetDateTime createTime;
    private OffsetDateTime updateTime;
    private UUID tenantId;
}
