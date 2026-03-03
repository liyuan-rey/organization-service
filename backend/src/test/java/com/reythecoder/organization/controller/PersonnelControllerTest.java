package com.reythecoder.organization.controller;

import tools.jackson.databind.ObjectMapper;
import com.reythecoder.organization.dto.request.PersonnelCreateReq;
import com.reythecoder.organization.dto.request.PersonnelUpdateReq;
import com.reythecoder.organization.dto.response.PersonnelRsp;
import com.reythecoder.organization.service.PersonnelService;

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

@WebMvcTest(PersonnelController.class)
class PersonnelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PersonnelService personnelService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID personnelId;
    private PersonnelRsp personnelRsp;
    private PersonnelCreateReq personnelCreateReq;
    private PersonnelUpdateReq personnelUpdateReq;

    @BeforeEach
    void setUp() {
        personnelId = UUIDv7.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        UUID tenantId = UUIDv7.randomUUID();

        personnelRsp = new PersonnelRsp(
                personnelId,
                "张三",
                "M",
                "110101199001011234",
                "13800138000",
                "010-12345678",
                "010-87654321",
                "zhangsan@example.com",
                now,
                now,
                tenantId);

        personnelCreateReq = new PersonnelCreateReq(
                "张三",
                "M",
                "110101199001011234",
                "13800138000",
                "010-12345678",
                "010-87654321",
                "zhangsan@example.com");

        personnelUpdateReq = new PersonnelUpdateReq(
                "李四",
                "F",
                "110101199001015678",
                "13900139000",
                "010-87654321",
                "010-12345678",
                "lisi@example.com");
    }

    @Test
    void getAllPersonnel_shouldReturnAllPersonnel() throws Exception {
        // Arrange
        List<PersonnelRsp> personnelList = Collections.singletonList(personnelRsp);
        when(personnelService.getAllPersonnel()).thenReturn(personnelList);

        // Act & Assert
        mockMvc.perform(get("/api/personnel"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(personnelId.toString()))
                .andExpect(jsonPath("$.data[0].name").value("张三"));

        verify(personnelService, times(1)).getAllPersonnel();
    }

    @Test
    void getAllPersonnel_shouldReturnEmptyListWhenNoPersonnel() throws Exception {
        // Arrange
        when(personnelService.getAllPersonnel()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/personnel"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());

        verify(personnelService, times(1)).getAllPersonnel();
    }

    @Test
    void getPersonnelById_shouldReturnPersonnelWhenExists() throws Exception {
        // Arrange
        when(personnelService.getPersonnelById(personnelId)).thenReturn(personnelRsp);

        // Act & Assert
        mockMvc.perform(get("/api/personnel/{id}", personnelId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.id").value(personnelId.toString()))
                .andExpect(jsonPath("$.data.name").value("张三"));

        verify(personnelService, times(1)).getPersonnelById(personnelId);
    }

    @Test
    void createPersonnel_shouldReturnCreatedPersonnel() throws Exception {
        // Arrange
        when(personnelService.createPersonnel(personnelCreateReq)).thenReturn(personnelRsp);

        // Act & Assert
        mockMvc.perform(post("/api/personnel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(personnelCreateReq)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("人员创建成功"))
                .andExpect(jsonPath("$.data.id").value(personnelId.toString()))
                .andExpect(jsonPath("$.data.name").value("张三"));

        verify(personnelService, times(1)).createPersonnel(personnelCreateReq);
    }

    @Test
    void updatePersonnel_shouldReturnUpdatedPersonnel() throws Exception {
        // Arrange
        PersonnelRsp updatedRsp = new PersonnelRsp(
                personnelId,
                "李四",
                "F",
                "110101199001015678",
                "13900139000",
                "010-87654321",
                "010-12345678",
                "lisi@example.com",
                personnelRsp.getCreateTime(),
                OffsetDateTime.now(),
                personnelRsp.getTenantId());

        when(personnelService.updatePersonnel(personnelId, personnelUpdateReq)).thenReturn(updatedRsp);

        // Act & Assert
        mockMvc.perform(put("/api/personnel/{id}", personnelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(personnelUpdateReq)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("人员更新成功"))
                .andExpect(jsonPath("$.data.id").value(personnelId.toString()))
                .andExpect(jsonPath("$.data.name").value("李四"))
                .andExpect(jsonPath("$.data.mobile").value("13900139000"));

        verify(personnelService, times(1)).updatePersonnel(personnelId, personnelUpdateReq);
    }

    @Test
    void deletePersonnel_shouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(personnelService).deletePersonnel(personnelId);

        // Act & Assert
        mockMvc.perform(delete("/api/personnel/{id}", personnelId))
                .andExpect(status().isNoContent());

        verify(personnelService, times(1)).deletePersonnel(personnelId);
    }
}