package com.reythecoder.organization.service;

import com.reythecoder.organization.dto.request.PositionCreateReq;
import com.reythecoder.organization.dto.request.PositionUpdateReq;
import com.reythecoder.organization.dto.response.PositionRsp;
import com.reythecoder.organization.entity.PositionEntity;
import com.reythecoder.common.exception.ApiException;
import com.reythecoder.organization.repository.PositionRepository;
import com.reythecoder.organization.service.impl.PositionServiceImpl;

import io.github.robsonkades.uuidv7.UUIDv7;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PositionServiceTest {

    @Mock
    private PositionRepository positionRepository;

    @InjectMocks
    private PositionServiceImpl positionService;

    private UUID positionId;
    private PositionEntity positionEntity;
    private PositionRsp positionRsp;
    private PositionCreateReq positionCreateReq;
    private PositionUpdateReq positionUpdateReq;

    @BeforeEach
    void setUp() {
        positionId = UUIDv7.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        UUID tenantId = UUIDv7.randomUUID();

        positionEntity = new PositionEntity(
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
    void getAllPositions_shouldReturnAllPositions() {
        // Arrange
        when(positionRepository.findAll()).thenReturn(List.of(positionEntity));

        // Act
        List<PositionRsp> result = positionService.getAllPositions();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(positionEntity.getId());
        assertThat(result.get(0).getName()).isEqualTo(positionEntity.getName());
        verify(positionRepository, times(1)).findAll();
    }

    @Test
    void getAllPositions_shouldReturnEmptyListWhenNoPositions() {
        // Arrange
        when(positionRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<PositionRsp> result = positionService.getAllPositions();

        // Assert
        assertThat(result).isEmpty();
        verify(positionRepository, times(1)).findAll();
    }

    @Test
    void getPositionById_shouldReturnPositionWhenExists() {
        // Arrange
        when(positionRepository.findById(positionId)).thenReturn(Optional.of(positionEntity));

        // Act
        PositionRsp result = positionService.getPositionById(positionId);

        // Assert
        assertThat(result.getId()).isEqualTo(positionEntity.getId());
        assertThat(result.getName()).isEqualTo(positionEntity.getName());
        verify(positionRepository, times(1)).findById(positionId);
    }

    @Test
    void getPositionById_shouldThrowExceptionWhenPositionNotFound() {
        // Arrange
        when(positionRepository.findById(positionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> positionService.getPositionById(positionId))
                .isInstanceOf(ApiException.class)
                .hasMessage("岗位不存在");
        verify(positionRepository, times(1)).findById(positionId);
    }

    @Test
    void createPosition_shouldReturnCreatedPosition() {
        // Arrange
        when(positionRepository.save(any(PositionEntity.class))).thenReturn(positionEntity);

        // Act
        PositionRsp result = positionService.createPosition(positionCreateReq);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(positionEntity.getId());
        assertThat(result.getName()).isEqualTo(positionEntity.getName());
        verify(positionRepository, times(1)).save(any(PositionEntity.class));
    }

    @Test
    void createPosition_shouldThrowExceptionWhenCodeExists() {
        // Arrange
        when(positionRepository.findByCode(positionCreateReq.getCode()))
                .thenReturn(List.of(positionEntity));

        // Act & Assert
        assertThatThrownBy(() -> positionService.createPosition(positionCreateReq))
                .isInstanceOf(ApiException.class)
                .hasMessage("岗位编码已存在");
        verify(positionRepository, never()).save(any());
    }

    @Test
    void updatePosition_shouldReturnUpdatedPositionWhenExists() {
        // Arrange
        PositionEntity updatedEntity = new PositionEntity(
                positionId,
                "高级软件工程师",
                "POS-SENR-001",
                "负责高级软件开发和架构设计",
                "P3",
                "Technical",
                new BigDecimal("20000.00"),
                new BigDecimal("35000.00"),
                1,
                positionEntity.getCreateTime(),
                OffsetDateTime.now(),
                positionEntity.getTenantId());

        when(positionRepository.findById(positionId)).thenReturn(Optional.of(positionEntity));
        when(positionRepository.save(positionEntity)).thenReturn(updatedEntity);

        // Act
        PositionRsp result = positionService.updatePosition(positionId, positionUpdateReq);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(updatedEntity.getId());
        assertThat(result.getName()).isEqualTo(updatedEntity.getName());
        assertThat(result.getJobLevel()).isEqualTo(updatedEntity.getJobLevel());
        verify(positionRepository, times(1)).findById(positionId);
        verify(positionRepository, times(1)).save(positionEntity);
    }

    @Test
    void updatePosition_shouldThrowExceptionWhenPositionNotFound() {
        // Arrange
        when(positionRepository.findById(positionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> positionService.updatePosition(positionId, positionUpdateReq))
                .isInstanceOf(ApiException.class)
                .hasMessage("岗位不存在");
        verify(positionRepository, times(1)).findById(positionId);
        verify(positionRepository, never()).save(any());
    }

    @Test
    void deletePosition_shouldDeletePositionWhenExists() {
        // Arrange
        when(positionRepository.findById(positionId)).thenReturn(Optional.of(positionEntity));

        // Act
        positionService.deletePosition(positionId);

        // Assert
        verify(positionRepository, times(1)).findById(positionId);
        verify(positionRepository, times(1)).delete(positionEntity);
    }

    @Test
    void deletePosition_shouldThrowExceptionWhenPositionNotFound() {
        // Arrange
        when(positionRepository.findById(positionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> positionService.deletePosition(positionId))
                .isInstanceOf(ApiException.class)
                .hasMessage("岗位不存在");
        verify(positionRepository, times(1)).findById(positionId);
        verify(positionRepository, never()).delete(any());
    }
}
