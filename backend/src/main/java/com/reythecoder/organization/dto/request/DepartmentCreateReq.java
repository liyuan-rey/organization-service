package com.reythecoder.organization.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentCreateReq {
    @NotBlank(message = "部门名称不能为空")
    @Size(max = 255, message = "部门名称长度不能超过 255 个字符")
    private String name;

    @Size(max = 255, message = "部门英文名称长度不能超过 255 个字符")
    private String englishName;

    @Size(max = 100, message = "部门简称长度不能超过 100 个字符")
    private String shortName;

    @Size(max = 50, message = "组织代码长度不能超过 50 个字符")
    private String orgCode;

    @Size(max = 50, message = "联系电话长度不能超过 50 个字符")
    private String phone;

    @Size(max = 50, message = "传真长度不能超过 50 个字符")
    private String fax;

    @Size(max = 100, message = "邮箱长度不能超过 100 个字符")
    private String email;

    @Size(max = 500, message = "地址长度不能超过 500 个字符")
    private String address;

    @Size(max = 20, message = "邮政编码长度不能超过 20 个字符")
    private String postalCode;
}
