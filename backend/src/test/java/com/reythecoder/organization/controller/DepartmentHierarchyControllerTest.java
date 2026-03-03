package com.reythecoder.organization.controller;

import tools.jackson.databind.ObjectMapper;
import com.reythecoder.organization.dto.request.DepartmentHierarchyCreateReq;
import com.reythecoder.organization.dto.response.DepartmentHierarchyRsp;
import com.reythecoder.organization.service.DepartmentHierarchyService;

import io.github.robsonkades.uuidv7.UUIDv7;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
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

@WebMvcTest(DepartmentHierarchyController.class)
class DepartmentHierarchyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DepartmentHierarchyService hierarchyService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID parentId;
    private UUID childId;
    private DepartmentHierarchyRsp hierarchyRsp;
    private DepartmentHierarchyCreateReq createReq;

    @BeforeEach
    void setUp() {
        parentId = UUIDv7.randomUUID();
        childId = UUIDv7.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        hierarchyRsp = new DepartmentHierarchyRsp();
        hierarchyRsp.setId(UUIDv7.randomUUID());
        hierarchyRsp.setParentId(parentId);
        hierarchyRsp.setChildId(childId);
        hierarchyRsp.setLevel(2);
        hierarchyRsp.setPath("/root/parent/" + childId + "/");
        hierarchyRsp.setSortOrder(1);
        hierarchyRsp.setChildName("子部门");
        hierarchyRsp.setCreateTime(now);
        hierarchyRsp.setUpdateTime(now);

        createReq = new DepartmentHierarchyCreateReq(
                parentId,
                childId,
                2,
                "/root/parent/" + childId + "/",
                1);
    }

    @Test
    void getRootDepartments_shouldReturnRoots() throws Exception {
        when(hierarchyService.getRootDepartments()).thenReturn(List.of(hierarchyRsp));

        mockMvc.perform(get("/api/department-hierarchy/roots"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].childId").value(childId.toString()));

        verify(hierarchyService, times(1)).getRootDepartments();
    }

    @Test
    void getChildren_shouldReturnChildren() throws Exception {
        when(hierarchyService.getChildrenByParentId(parentId)).thenReturn(List.of(hierarchyRsp));

        mockMvc.perform(get("/api/department-hierarchy/children/{parentId}", parentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].parentId").value(parentId.toString()));

        verify(hierarchyService, times(1)).getChildrenByParentId(parentId);
    }

    @Test
    void getByChildId_shouldReturnHierarchy() throws Exception {
        when(hierarchyService.getByChildId(childId)).thenReturn(hierarchyRsp);

        mockMvc.perform(get("/api/department-hierarchy/{childId}", childId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.childId").value(childId.toString()));

        verify(hierarchyService, times(1)).getByChildId(childId);
    }

    @Test
    void create_shouldReturnCreatedHierarchy() throws Exception {
        when(hierarchyService.create(createReq)).thenReturn(hierarchyRsp);

        mockMvc.perform(post("/api/department-hierarchy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("部门层级关系创建成功"));

        verify(hierarchyService, times(1)).create(createReq);
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        doNothing().when(hierarchyService).deleteByChildId(childId);

        mockMvc.perform(delete("/api/department-hierarchy/{childId}", childId))
                .andExpect(status().isNoContent());

        verify(hierarchyService, times(1)).deleteByChildId(childId);
    }
}