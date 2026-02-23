package com.reythecoder.organization.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonnelUpdateReq {
    @Size(max = 100, message = "姓名长度不能超过 100 个字符")
    private String name;

    @Size(max = 1, message = "性别只能是 M 或 F")
    private String gender;

    @Size(max = 18, message = "身份证号长度不能超过 18 个字符")
    private String idCard;

    @Size(max = 20, message = "手机号长度不能超过 20 个字符")
    private String mobile;

    @Size(max = 20, message = "电话长度不能超过 20 个字符")
    private String telephone;

    @Size(max = 20, message = "传真长度不能超过 20 个字符")
    private String fax;

    @Size(max = 100, message = "邮箱长度不能超过 100 个字符")
    private String email;
}
