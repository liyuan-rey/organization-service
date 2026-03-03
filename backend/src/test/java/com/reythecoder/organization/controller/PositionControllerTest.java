package com.reythecoder.organization.controller;

import tools.jackson.databind.ObjectMapper;
import com.reythecoder.organization.dto.request.PositionCreateReq;
import com.reythecoder.organization.dto.request.PositionUpdateReq;
import com.reythecoder.organization.dto.response.PositionRsp;
import com.reythecoder.organization.service.PositionService;

import io.github.robsonkades.uuidv7.UUIDv7;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PositionController.class)
class PositionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PositionService positionService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID positionId;
    private PositionRsp positionRsp;
    private PositionCreateReq positionCreateReq;
    private PositionUpdateReq positionUpdateReq;

    @BeforeEach
    void setUp() {
        positionId = UUIDv7.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        UUID tenantId = UUIDv7.randomUUID();

        positionRsp = new PositionRsp(
                positionId,
                "软件工程师",
                "POS-DEV-001",
                "负责软件开发工作",
                "P2",
                "Technical",
                new BigDecimal("10000.00"),
                new BigDecimal("20000.00"),
                1,
                now,
                now,
                tenantId);

        positionCreateReq = new PositionCreateReq(
                "软件工程师",
                "POS-DEV-001",
                "负责软件开发工作",
                "P2",
                "Technical",
                new BigDecimal("10000.00"),
                new BigDecimal("20000.00"),
                1);

        positionUpdateReq = new PositionUpdateReq(
                "高级软件工程师",
                "POS-SENR-001",
                "负责高级软件开发和架构设计",
                "P3",
                "Technical",
                new BigDecimal("20000.00"),
                new BigDecimal("35000.00"),
                1);
    }

    @Test
    void getAllPositions_shouldReturnAllPositions() throws Exception {
        // Arrange
        List<PositionRsp> positions = Collections.singletonList(positionRsp);
        when(positionService.getAllPositions()).thenReturn(positions);

        // Act & Assert
        mockMvc.perform(get("/api/positions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(positionId.toString()))
                .andExpect(jsonPath("$.data[0].name").value("软件工程师"));

        verify(positionService, times(1)).getAllPositions();
    }

    @Test
    void getAllPositions_shouldReturnEmptyListWhenNoPositions() throws Exception {
        // Arrange
        when(positionService.getAllPositions()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/positions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());

        verify(positionService, times(1)).getAllPositions();
    }

    @Test
    void getPositionById_shouldReturnPositionWhenExists() throws Exception {
        // Arrange
        when(positionService.getPositionById(positionId)).thenReturn(positionRsp);

        // Act & Assert
        mockMvc.perform(get("/api/positions/{id}", positionId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.id").value(positionId.toString()))
                .andExpect(jsonPath("$.data.name").value("软件工程师"));

        verify(positionService, times(1)).getPositionById(positionId);
    }

    @Test
    void createPosition_shouldReturnCreatedPosition() throws Exception {
        // Arrange
        when(positionService.createPosition(positionCreateReq)).thenReturn(positionRsp);

        // Act & Assert
        mockMvc.perform(post("/api/positions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(positionCreateReq)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("岗位创建成功"))
                .andExpect(jsonPath("$.data.id").value(positionId.toString()))
                .andExpect(jsonPath("$.data.name").value("软件工程师"));

        verify(positionService, times(1)).createPosition(positionCreateReq);
    }

    @Test
    void updatePosition_shouldReturnUpdatedPosition() throws Exception {
        // Arrange
        PositionRsp updatedRsp = new PositionRsp(
                positionId,
                "高级软件工程师",
                "POS-SENR-001",
                "负责高级软件开发和架构设计",
                "P3",
                "Technical",
                new BigDecimal("20000.00"),
                new BigDecimal("35000.00"),
                1,
                positionRsp.getCreateTime(),
                OffsetDateTime.now(),
                positionRsp.getTenantId());

        when(positionService.updatePosition(positionId, positionUpdateReq)).thenReturn(updatedRsp);

        // Act & Assert
        mockMvc.perform(put("/api/positions/{id}", positionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(positionUpdateReq)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("岗位更新成功"))
                .andExpect(jsonPath("$.data.id").value(positionId.toString()))
                .andExpect(jsonPath("$.data.name").value("高级软件工程师"))
                .andExpect(jsonPath("$.data.jobLevel").value("P3"));

        verify(positionService, times(1)).updatePosition(positionId, positionUpdateReq);
    }

    @Test
    void deletePosition_shouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(positionService).deletePosition(positionId);

        // Act & Assert
        mockMvc.perform(delete("/api/positions/{id}", positionId))
                .andExpect(status().isNoContent());

        verify(positionService, times(1)).deletePosition(positionId);
    }
}
