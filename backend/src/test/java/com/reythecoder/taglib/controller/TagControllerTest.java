package com.reythecoder.taglib.controller;

import tools.jackson.databind.ObjectMapper;
import com.reythecoder.taglib.dto.request.TagCreateReq;
import com.reythecoder.taglib.dto.request.TagUpdateReq;
import com.reythecoder.taglib.dto.response.TagRsp;
import com.reythecoder.taglib.dto.response.TagTreeRsp;
import com.reythecoder.taglib.service.TagService;

import io.github.robsonkades.uuidv7.UUIDv7;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TagController.class)
@Import(TagController.class)
class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TagService tagService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID categoryId;
    private UUID tagId;
    private TagRsp tagRsp;
    private TagCreateReq createReq;
    private TagUpdateReq updateReq;

    @BeforeEach
    void setUp() {
        categoryId = UUIDv7.randomUUID();
        tagId = UUIDv7.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        tagRsp = new TagRsp(
                tagId,
                "测试标签",
                categoryId,
                "测试分类",
                null,
                "a0",
                now,
                now);

        createReq = new TagCreateReq("测试标签", categoryId, null, null);
        updateReq = new TagUpdateReq("更新标签", null, "b0");
    }

    @Test
    void getTagTree_shouldReturnTree() throws Exception {
        // Arrange
        TagTreeRsp treeRsp = new TagTreeRsp(tagId, "测试标签", categoryId, "测试分类", null, "a0", Collections.emptyList());
        when(tagService.getTagTreeByCategory(categoryId)).thenReturn(List.of(treeRsp));

        // Act & Assert
        mockMvc.perform(get("/api/tags")
                .param("categoryId", categoryId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(tagId.toString()))
                .andExpect(jsonPath("$.data[0].name").value("测试标签"));

        verify(tagService, times(1)).getTagTreeByCategory(categoryId);
    }

    @Test
    void getTagById_shouldReturnTag() throws Exception {
        // Arrange
        when(tagService.getById(tagId)).thenReturn(tagRsp);

        // Act & Assert
        mockMvc.perform(get("/api/tags/{id}", tagId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(tagId.toString()))
                .andExpect(jsonPath("$.data.name").value("测试标签"));

        verify(tagService, times(1)).getById(tagId);
    }

    @Test
    void createTag_shouldReturnCreated() throws Exception {
        // Arrange
        when(tagService.create(createReq)).thenReturn(tagRsp);

        // Act & Assert
        mockMvc.perform(post("/api/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(tagId.toString()))
                .andExpect(jsonPath("$.data.name").value("测试标签"));

        verify(tagService, times(1)).create(createReq);
    }

    @Test
    void updateTag_shouldReturnUpdated() throws Exception {
        // Arrange
        TagRsp updatedRsp = new TagRsp(
                tagId,
                "更新标签",
                categoryId,
                "测试分类",
                null,
                "b0",
                tagRsp.getCreateTime(),
                OffsetDateTime.now());

        when(tagService.update(tagId, updateReq)).thenReturn(updatedRsp);

        // Act & Assert
        mockMvc.perform(put("/api/tags/{id}", tagId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(tagId.toString()))
                .andExpect(jsonPath("$.data.name").value("更新标签"));

        verify(tagService, times(1)).update(tagId, updateReq);
    }

    @Test
    void deleteTag_shouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(tagService).delete(tagId);

        // Act & Assert
        mockMvc.perform(delete("/api/tags/{id}", tagId))
                .andExpect(status().isNoContent());

        verify(tagService, times(1)).delete(tagId);
    }
}
