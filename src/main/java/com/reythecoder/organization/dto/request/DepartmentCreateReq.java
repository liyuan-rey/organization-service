package com.reythecoder.organization.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DepartmentCreateReq(
        @NotBlank(message = "部门名称不能为空") @Size(max = 255, message = "部门名称长度不能超过255个字符") String name,

        @Size(max = 255, message = "部门英文名称长度不能超过255个字符") String englishName,

        @Size(max = 100, message = "部门简称长度不能超过100个字符") String shortName,

        @Size(max = 50, message = "组织代码长度不能超过50个字符") String orgCode,

        @Size(max = 50, message = "联系电话长度不能超过50个字符") String phone,

        @Size(max = 50, message = "传真长度不能超过50个字符") String fax,

        @Size(max = 100, message = "邮箱长度不能超过100个字符") String email,

        @Size(max = 500, message = "地址长度不能超过500个字符") String address,

        @Size(max = 20, message = "邮政编码长度不能超过20个字符") String postalCode) {
}
