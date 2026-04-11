package com.reythecoder.taglib.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagCreateReq {

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotNull
    private UUID categoryId;

    private UUID parentId;

    private String sortRank;
}
