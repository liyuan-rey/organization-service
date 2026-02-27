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
public class DepartmentRsp {
    private UUID id;
    private String name;
    private String englishName;
    private String shortName;
    private String orgCode;
    private String phone;
    private String fax;
    private String email;
    private String address;
    private String postalCode;
    private OffsetDateTime createTime;
    private OffsetDateTime updateTime;
    private UUID tenantId;
    
    /**
     * 部门内人数
     */
    @Builder.Default
    private Integer personCount = 0;
}
