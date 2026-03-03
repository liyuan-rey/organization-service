package com.reythecoder.organization.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupUpdateReq {
    
    private String name;
    
    private String description;
}