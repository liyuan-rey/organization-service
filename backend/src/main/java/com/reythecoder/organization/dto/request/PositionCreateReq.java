package com.reythecoder.organization.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PositionCreateReq {
    
    @NotBlank(message = "岗位名称不能为空")
    @Size(max = 100, message = "岗位名称长度不能超过 100 个字符")
    private String name;
    
    @NotBlank(message = "岗位编码不能为空")
    @Size(max = 50, message = "岗位编码长度不能超过 50 个字符")
    private String code;
    
    @Size(max = 500, message = "岗位描述长度不能超过 500 个字符")
    private String description;
    
    @Size(max = 50, message = "职级长度不能超过 50 个字符")
    private String jobLevel;
    
    @Size(max = 50, message = "岗位类别长度不能超过 50 个字符")
    private String jobCategory;
    
    private BigDecimal minSalary;
    
    private BigDecimal maxSalary;
    
    private Integer status;
}
