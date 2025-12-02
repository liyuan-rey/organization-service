package com.reythecoder.organization.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DepartmentRsp(
        UUID id,
        String name,
        String englishName,
        String shortName,
        String orgCode,
        String phone,
        String fax,
        String email,
        String address,
        String postalCode,
        OffsetDateTime createTime,
        OffsetDateTime updateTime,
        UUID tenantId) {
}
