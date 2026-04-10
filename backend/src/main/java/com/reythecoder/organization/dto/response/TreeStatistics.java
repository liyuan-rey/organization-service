package com.reythecoder.organization.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Tree statistics response DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreeStatistics {
    private Integer subGroupCount;
    private Integer subDepartmentCount;
    private Integer personnelCount;
}