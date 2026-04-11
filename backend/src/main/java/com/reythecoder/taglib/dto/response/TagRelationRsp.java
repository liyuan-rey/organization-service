package com.reythecoder.taglib.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagRelationRsp {

    private UUID id;
    private String objectType;
    private UUID objectId;
    private String objectName;
    private UUID tagId;
    private String tagName;
    private OffsetDateTime createTime;
    private OffsetDateTime updateTime;
}
