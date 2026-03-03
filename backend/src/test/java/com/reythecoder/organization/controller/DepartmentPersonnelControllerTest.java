package com.reythecoder.organization.controller;

import tools.jackson.databind.ObjectMapper;
import com.reythecoder.organization.dto.request.DepartmentPersonnelCreateReq;
import com.reythecoder.organization.dto.response.DepartmentPersonnelRsp;
import com.reythecoder.organization.service.DepartmentPersonnelService;

import io.github.robsonkades.uuidv7.UUIDv7;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DepartmentPersonnelController.class)
class DepartmentPersonnelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DepartmentPersonnelService departmentPersonnelService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID departmentId;
    private UUID personnelId;
    private DepartmentPersonnelRsp personnelRsp;
    private DepartmentPersonnelCreateReq createReq;

    @BeforeEach
    void setUp() {
        departmentId = UUIDv7.randomUUID();
        personnelId = UUIDv7.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        personnelRsp = new DepartmentPersonnelRsp();
        personnelRsp.setId(UUIDv7.randomUUID());
        personnelRsp.setDepartmentId(departmentId);
        personnelRsp.setPersonnelId(personnelId);
        personnelRsp.setIsPrimary(true);
        personnelRsp.setPosition("经理");
        personnelRsp.setSortOrder(1);
        personnelRsp.setDepartmentName("技术部");
        personnelRsp.setPersonnelName("张三");
        personnelRsp.setCreateTime(now);
        personnelRsp.setUpdateTime(now);

        createReq = new DepartmentPersonnelCreateReq(
                departmentId,
                personnelId,
                true,
                "经理",
                1);
    }

    @Test
    void getByDepartmentId_shouldReturnPersonnelList() throws Exception {
        when(departmentPersonnelService.getByDepartmentId(departmentId)).thenReturn(List.of(personnelRsp));

        mockMvc.perform(get("/api/department-personnel/department/{departmentId}", departmentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].personnelId").value(personnelId.toString()));

        verify(departmentPersonnelService, times(1)).getByDepartmentId(departmentId);
    }

    @Test
    void getByPersonnelId_shouldReturnDepartmentList() throws Exception {
        when(departmentPersonnelService.getByPersonnelId(personnelId)).thenReturn(List.of(personnelRsp));

        mockMvc.perform(get("/api/department-personnel/personnel/{personnelId}", personnelId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].departmentId").value(departmentId.toString()));

        verify(departmentPersonnelService, times(1)).getByPersonnelId(personnelId);
    }

    @Test
    void create_shouldReturnCreatedRelation() throws Exception {
        when(departmentPersonnelService.create(createReq)).thenReturn(personnelRsp);

        mockMvc.perform(post("/api/department-personnel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("部门人员关联创建成功"));

        verify(departmentPersonnelService, times(1)).create(createReq);
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        doNothing().when(departmentPersonnelService).delete(departmentId, personnelId);

        mockMvc.perform(delete("/api/department-personnel/{departmentId}/{personnelId}", departmentId, personnelId))
                .andExpect(status().isNoContent());

        verify(departmentPersonnelService, times(1)).delete(departmentId, personnelId);
    }

    @Test
    void setPrimaryDepartment_shouldReturnSuccess() throws Exception {
        doNothing().when(departmentPersonnelService).setPrimaryDepartment(personnelId, departmentId);

        mockMvc.perform(put("/api/department-personnel/set-primary/{personnelId}/{departmentId}", personnelId, departmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("主部门设置成功"));

        verify(departmentPersonnelService, times(1)).setPrimaryDepartment(personnelId, departmentId);
    }
}