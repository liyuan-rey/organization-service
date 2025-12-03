package com.reythecoder.organization.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reythecoder.organization.dto.request.DepartmentCreateReq;
import com.reythecoder.organization.dto.request.DepartmentUpdateReq;
import com.reythecoder.organization.dto.response.DepartmentRsp;
import com.reythecoder.organization.service.DepartmentService;

import io.github.robsonkades.uuidv7.UUIDv7;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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

@WebMvcTest(DepartmentController.class)
class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DepartmentService departmentService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID departmentId;
    private DepartmentRsp departmentRsp;
    private DepartmentCreateReq departmentCreateReq;
    private DepartmentUpdateReq departmentUpdateReq;

    @BeforeEach
    void setUp() {
        departmentId = UUIDv7.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        UUID tenantId = UUIDv7.randomUUID();

        departmentRsp = new DepartmentRsp(
                departmentId,
                "测试部门",
                "Test Department",
                "测试部",
                "TEST001",
                "123456789",
                "987654321",
                "test@example.com",
                "测试地址",
                "123456",
                now,
                now,
                tenantId);

        departmentCreateReq = new DepartmentCreateReq(
                "测试部门",
                "Test Department",
                "测试部",
                "TEST001",
                "123456789",
                "987654321",
                "test@example.com",
                "测试地址",
                "123456");

        departmentUpdateReq = new DepartmentUpdateReq(
                "更新后的部门",
                "Updated Department",
                "更新部",
                "TEST002",
                "987654321",
                "123456789",
                "updated@example.com",
                "更新后的地址",
                "654321");
    }

    @Test
    void getAllDepartments_shouldReturnAllDepartments() throws Exception {
        // Arrange
        List<DepartmentRsp> departments = Collections.singletonList(departmentRsp);
        when(departmentService.getAllDepartments()).thenReturn(departments);

        // Act & Assert
        mockMvc.perform(get("/api/departments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(departmentId.toString()))
                .andExpect(jsonPath("$.data[0].name").value("测试部门"));

        verify(departmentService, times(1)).getAllDepartments();
    }

    @Test
    void getAllDepartments_shouldReturnEmptyListWhenNoDepartments() throws Exception {
        // Arrange
        when(departmentService.getAllDepartments()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/departments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());

        verify(departmentService, times(1)).getAllDepartments();
    }

    @Test
    void getDepartmentById_shouldReturnDepartmentWhenExists() throws Exception {
        // Arrange
        when(departmentService.getDepartmentById(departmentId)).thenReturn(departmentRsp);

        // Act & Assert
        mockMvc.perform(get("/api/departments/{id}", departmentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.id").value(departmentId.toString()))
                .andExpect(jsonPath("$.data.name").value("测试部门"));

        verify(departmentService, times(1)).getDepartmentById(departmentId);
    }

    @Test
    void createDepartment_shouldReturnCreatedDepartment() throws Exception {
        // Arrange
        when(departmentService.createDepartment(departmentCreateReq)).thenReturn(departmentRsp);

        // Act & Assert
        mockMvc.perform(post("/api/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(departmentCreateReq)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("部门创建成功"))
                .andExpect(jsonPath("$.data.id").value(departmentId.toString()))
                .andExpect(jsonPath("$.data.name").value("测试部门"));

        verify(departmentService, times(1)).createDepartment(departmentCreateReq);
    }

    @Test
    void updateDepartment_shouldReturnUpdatedDepartment() throws Exception {
        // Arrange
        DepartmentRsp updatedRsp = new DepartmentRsp(
                departmentId,
                "更新后的部门",
                "Updated Department",
                "更新部",
                "TEST002",
                "987654321",
                "123456789",
                "updated@example.com",
                "更新后的地址",
                "654321",
                departmentRsp.createTime(),
                OffsetDateTime.now(),
                departmentRsp.tenantId());

        when(departmentService.updateDepartment(departmentId, departmentUpdateReq)).thenReturn(updatedRsp);

        // Act & Assert
        mockMvc.perform(put("/api/departments/{id}", departmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(departmentUpdateReq)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("部门更新成功"))
                .andExpect(jsonPath("$.data.id").value(departmentId.toString()))
                .andExpect(jsonPath("$.data.name").value("更新后的部门"))
                .andExpect(jsonPath("$.data.orgCode").value("TEST002"));

        verify(departmentService, times(1)).updateDepartment(departmentId, departmentUpdateReq);
    }

    @Test
    void deleteDepartment_shouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(departmentService).deleteDepartment(departmentId);

        // Act & Assert
        mockMvc.perform(delete("/api/departments/{id}", departmentId))
                .andExpect(status().isNoContent());

        verify(departmentService, times(1)).deleteDepartment(departmentId);
    }
}
