package com.reythecoder.taglib.controller;

import tools.jackson.databind.ObjectMapper;
import com.reythecoder.taglib.dto.request.TagCategoryCreateReq;
import com.reythecoder.taglib.dto.request.TagCategoryUpdateReq;
import com.reythecoder.taglib.dto.response.TagCategoryRsp;
import com.reythecoder.taglib.service.TagCategoryService;

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

@WebMvcTest(TagCategoryController.class)
@Import(TagCategoryController.class)
class TagCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TagCategoryService tagCategoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID categoryId;
    private TagCategoryRsp tagCategoryRsp;
    private TagCategoryCreateReq createReq;
    private TagCategoryUpdateReq updateReq;

    @BeforeEach
    void setUp() {
        categoryId = UUIDv7.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        tagCategoryRsp = new TagCategoryRsp(
                categoryId,
                "测试分类",
                "测试分类描述",
                "a0",
                now,
                now);

        createReq = new TagCategoryCreateReq("测试分类", "测试分类描述", null);
        updateReq = new TagCategoryUpdateReq("更新分类", "更新描述", "b0");
    }

    @Test
    void getAllCategories_shouldReturnList() throws Exception {
        // Arrange
        List<TagCategoryRsp> categories = Collections.singletonList(tagCategoryRsp);
        when(tagCategoryService.getAllCategories()).thenReturn(categories);

        // Act & Assert
        mockMvc.perform(get("/api/tag-categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(categoryId.toString()))
                .andExpect(jsonPath("$.data[0].name").value("测试分类"));

        verify(tagCategoryService, times(1)).getAllCategories();
    }

    @Test
    void getCategoryById_shouldReturnCategory() throws Exception {
        // Arrange
        when(tagCategoryService.getById(categoryId)).thenReturn(tagCategoryRsp);

        // Act & Assert
        mockMvc.perform(get("/api/tag-categories/{id}", categoryId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(categoryId.toString()))
                .andExpect(jsonPath("$.data.name").value("测试分类"));

        verify(tagCategoryService, times(1)).getById(categoryId);
    }

    @Test
    void createCategory_shouldReturnCreated() throws Exception {
        // Arrange
        when(tagCategoryService.create(createReq)).thenReturn(tagCategoryRsp);

        // Act & Assert
        mockMvc.perform(post("/api/tag-categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(categoryId.toString()))
                .andExpect(jsonPath("$.data.name").value("测试分类"));

        verify(tagCategoryService, times(1)).create(createReq);
    }

    @Test
    void updateCategory_shouldReturnUpdated() throws Exception {
        // Arrange
        TagCategoryRsp updatedRsp = new TagCategoryRsp(
                categoryId,
                "更新分类",
                "更新描述",
                "b0",
                tagCategoryRsp.getCreateTime(),
                OffsetDateTime.now());

        when(tagCategoryService.update(categoryId, updateReq)).thenReturn(updatedRsp);

        // Act & Assert
        mockMvc.perform(put("/api/tag-categories/{id}", categoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(categoryId.toString()))
                .andExpect(jsonPath("$.data.name").value("更新分类"));

        verify(tagCategoryService, times(1)).update(categoryId, updateReq);
    }

    @Test
    void deleteCategory_shouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(tagCategoryService).delete(categoryId);

        // Act & Assert
        mockMvc.perform(delete("/api/tag-categories/{id}", categoryId))
                .andExpect(status().isNoContent());

        verify(tagCategoryService, times(1)).delete(categoryId);
    }
}
