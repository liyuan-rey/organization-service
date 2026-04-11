package com.reythecoder.taglib.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagCategoryRsp {

    private UUID id;
    private String name;
    private String description;
    private String sortRank;
    private OffsetDateTime createTime;
    private OffsetDateTime updateTime;
}
