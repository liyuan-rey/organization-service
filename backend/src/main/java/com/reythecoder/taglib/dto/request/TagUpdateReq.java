package com.reythecoder.taglib.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagUpdateReq {

    @Size(max = 100)
    private String name;

    private UUID parentId;

    private String sortRank;
}
