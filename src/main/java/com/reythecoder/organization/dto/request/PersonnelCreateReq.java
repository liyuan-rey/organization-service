package com.reythecoder.organization.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PersonnelCreateReq(
        @NotBlank(message = "姓名不能为空") @Size(max = 100, message = "姓名长度不能超过100个字符") String name,

        @NotBlank(message = "性别不能为空") @Size(max = 1, message = "性别只能是M或F") String gender,

        @NotBlank(message = "身份证号不能为空") @Size(max = 18, message = "身份证号长度不能超过18个字符") String idCard,

        @NotBlank(message = "手机号不能为空") @Size(max = 20, message = "手机号长度不能超过20个字符") String mobile,

        @Size(max = 20, message = "电话长度不能超过20个字符") String telephone,

        @Size(max = 20, message = "传真长度不能超过20个字符") String fax,

        @Size(max = 100, message = "邮箱长度不能超过100个字符") String email) {
}