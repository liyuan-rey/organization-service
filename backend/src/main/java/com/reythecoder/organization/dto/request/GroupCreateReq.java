package com.reythecoder.organization.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupCreateReq {
    
    @NotBlank(message = "分组名称不能为空")
    private String name;
    
    private String description;
}