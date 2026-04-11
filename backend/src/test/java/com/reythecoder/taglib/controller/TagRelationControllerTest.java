package com.reythecoder.taglib.controller;

import tools.jackson.databind.ObjectMapper;
import com.reythecoder.taglib.dto.request.TagRelationQueryReq;
import com.reythecoder.taglib.dto.request.TagRelationReq;
import com.reythecoder.taglib.dto.response.TagRelationRsp;
import com.reythecoder.taglib.service.TagRelationService;

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

@Import(com.reythecoder.common.exception.GlobalExceptionHandler.class)
@WebMvcTest(TagRelationController.class)
class TagRelationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TagRelationService tagRelationService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID relationId;
    private UUID tagId;
    private UUID objectId;
    private TagRelationRsp tagRelationRsp;

    @BeforeEach
    void setUp() {
        relationId = UUIDv7.randomUUID();
        tagId = UUIDv7.randomUUID();
        objectId = UUIDv7.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        tagRelationRsp = new TagRelationRsp(
                relationId,
                "DEPARTMENT",
                objectId,
                "",
                tagId,
                "测试标签",
                now,
                now);
    }

    @Test
    void getByObject_shouldReturnList() throws Exception {
        // Arrange
        List<TagRelationRsp> relations = Collections.singletonList(tagRelationRsp);
        when(tagRelationService.getByObject("DEPARTMENT", objectId)).thenReturn(relations);

        // Act & Assert
        mockMvc.perform(get("/api/tag-relations")
                .param("objectType", "DEPARTMENT")
                .param("objectId", objectId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(relationId.toString()))
                .andExpect(jsonPath("$.data[0].objectType").value("DEPARTMENT"));

        verify(tagRelationService, times(1)).getByObject("DEPARTMENT", objectId);
    }

    @Test
    void getByTag_shouldReturnList() throws Exception {
        // Arrange
        List<TagRelationRsp> relations = Collections.singletonList(tagRelationRsp);
        when(tagRelationService.getByTag(tagId)).thenReturn(relations);

        // Act & Assert
        mockMvc.perform(get("/api/tag-relations")
                .param("tagId", tagId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(relationId.toString()));

        verify(tagRelationService, times(1)).getByTag(tagId);
    }

    @Test
    void batchCreate_shouldReturnCreated() throws Exception {
        // Arrange
        TagRelationReq req = new TagRelationReq("DEPARTMENT", objectId, List.of(tagId));
        List<TagRelationRsp> relations = Collections.singletonList(tagRelationRsp);
        when(tagRelationService.batchCreate(any(TagRelationReq.class))).thenReturn(relations);

        // Act & Assert
        mockMvc.perform(post("/api/tag-relations/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(relationId.toString()));

        verify(tagRelationService, times(1)).batchCreate(any(TagRelationReq.class));
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(tagRelationService).delete(relationId);

        // Act & Assert
        mockMvc.perform(delete("/api/tag-relations/{id}", relationId))
                .andExpect(status().isNoContent());

        verify(tagRelationService, times(1)).delete(relationId);
    }

    @Test
    void queryByMultipleTags_shouldReturnResults() throws Exception {
        // Arrange
        TagRelationQueryReq req = new TagRelationQueryReq("DEPARTMENT", List.of(tagId));
        List<TagRelationRsp> relations = Collections.singletonList(tagRelationRsp);
        when(tagRelationService.queryByMultipleTags(any(TagRelationQueryReq.class))).thenReturn(relations);

        // Act & Assert
        mockMvc.perform(post("/api/tag-relations/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(relationId.toString()));

        verify(tagRelationService, times(1)).queryByMultipleTags(any(TagRelationQueryReq.class));
    }
}
