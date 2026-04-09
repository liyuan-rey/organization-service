package com.reythecoder.organization.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreeStatistics {
    private Integer subGroupCount;
    private Integer subDepartmentCount;
    private Integer personnelCount;
}