package com.reythecoder.taglib.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagTreeRsp {

    private UUID id;
    private String name;
    private UUID categoryId;
    private String categoryName;
    private UUID parentId;
    private String sortRank;
    private List<TagTreeRsp> children;
}
