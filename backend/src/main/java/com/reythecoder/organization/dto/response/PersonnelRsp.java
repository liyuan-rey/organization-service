package com.reythecoder.organization.dto.response;

import lombok.Data;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class PersonnelRsp {
    private UUID id;
    private String name;
    private String gender;
    private String idCard;
    private String mobile;
    private String telephone;
    private String fax;
    private String email;
    private OffsetDateTime createTime;
    private OffsetDateTime updateTime;
    private UUID tenantId;

    public PersonnelRsp() {}

    public PersonnelRsp(UUID id, String name, String gender, String idCard, String mobile,
                       String telephone, String fax, String email, OffsetDateTime createTime,
                       OffsetDateTime updateTime, UUID tenantId) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.idCard = idCard;
        this.mobile = mobile;
        this.telephone = telephone;
        this.fax = fax;
        this.email = email;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.tenantId = tenantId;
    }
}