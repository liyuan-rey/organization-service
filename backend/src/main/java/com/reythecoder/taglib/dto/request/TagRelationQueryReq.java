package com.reythecoder.taglib.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagRelationQueryReq {

    @NotNull
    private String objectType;

    @NotEmpty
    private List<UUID> tagIds;
}
