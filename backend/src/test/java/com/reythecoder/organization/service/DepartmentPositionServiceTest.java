package com.reythecoder.organization.service;

import com.reythecoder.organization.dto.request.DepartmentPositionReq;
import com.reythecoder.organization.dto.response.DepartmentPositionRsp;
import com.reythecoder.organization.entity.DepartmentEntity;
import com.reythecoder.organization.entity.DepartmentPositionEntity;
import com.reythecoder.organization.entity.PositionEntity;
import com.reythecoder.organization.exception.ApiException;
import com.reythecoder.organization.repository.DepartmentPositionRepository;
import com.reythecoder.organization.repository.DepartmentRepository;
import com.reythecoder.organization.repository.PositionRepository;
import com.reythecoder.organization.service.impl.DepartmentPositionServiceImpl;

import io.github.robsonkades.uuidv7.UUIDv7;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class DepartmentPositionServiceTest {

    @Mock
    private DepartmentPositionRepository departmentPositionRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private PositionRepository positionRepository;

    @InjectMocks
    private DepartmentPositionServiceImpl departmentPositionService;

    private UUID departmentId;
    private UUID positionId;
    private UUID relationId;
    private DepartmentEntity departmentEntity;
    private PositionEntity positionEntity;
    private DepartmentPositionEntity relationEntity;
    private DepartmentPositionReq relationReq;

    @BeforeEach
    void setUp() {
        departmentId = UUIDv7.randomUUID();
        positionId = UUIDv7.randomUUID();
        relationId = UUIDv7.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        UUID tenantId = UUIDv7.randomUUID();

        departmentEntity = new DepartmentEntity(
                departmentId,
                "技术部",
                "Technology Department",
                "技术",
                "TECH-001",
                "123456789",
                "987654321",
                "tech@example.com",
                "科技园区",
                "100000",
                now,
                now,
                tenantId);

        positionEntity = new PositionEntity(
                positionId,
                "软件工程师",
                "POS-DEV-001",
                "负责软件开发",
                "P2",
                "Technical",
                null,
                null,
                1,
                now,
                now,
                tenantId);

        relationEntity = new DepartmentPositionEntity(
                relationId,
                departmentId,
                positionId,
                true,
                1,
                now,
                now,
                tenantId);

        relationReq = new DepartmentPositionReq(departmentId, positionId, true, 1);
    }

    @Test
    void getAllDepartmentPositions_shouldReturnAllRelations() {
        // Arrange
        when(departmentPositionRepository.findAll()).thenReturn(List.of(relationEntity));
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(departmentEntity));
        when(positionRepository.findById(positionId)).thenReturn(Optional.of(positionEntity));

        // Act
        List<DepartmentPositionRsp> result = departmentPositionService.getAllDepartmentPositions();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDepartmentId()).isEqualTo(departmentId);
        assertThat(result.get(0).getPositionId()).isEqualTo(positionId);
        verify(departmentPositionRepository, times(1)).findAll();
    }

    @Test
    void getPositionsByDepartmentId_shouldReturnPositionsForDepartment() {
        // Arrange
        when(departmentPositionRepository.findByDepartmentId(departmentId))
                .thenReturn(List.of(relationEntity));
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(departmentEntity));
        when(positionRepository.findById(positionId)).thenReturn(Optional.of(positionEntity));

        // Act
        List<DepartmentPositionRsp> result = departmentPositionService.getPositionsByDepartmentId(departmentId);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDepartmentId()).isEqualTo(departmentId);
        verify(departmentPositionRepository, times(1)).findByDepartmentId(departmentId);
    }

    @Test
    void getDepartmentsByPositionId_shouldReturnDepartmentsForPosition() {
        // Arrange
        when(departmentPositionRepository.findByPositionId(positionId))
                .thenReturn(List.of(relationEntity));
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(departmentEntity));
        when(positionRepository.findById(positionId)).thenReturn(Optional.of(positionEntity));

        // Act
        List<DepartmentPositionRsp> result = departmentPositionService.getDepartmentsByPositionId(positionId);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPositionId()).isEqualTo(positionId);
        verify(departmentPositionRepository, times(1)).findByPositionId(positionId);
    }

    @Test
    void createDepartmentPosition_shouldReturnCreatedRelation() {
        // Arrange
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(departmentEntity));
        when(positionRepository.findById(positionId)).thenReturn(Optional.of(positionEntity));
        when(departmentPositionRepository.findByDepartmentIdAndPositionId(departmentId, positionId))
                .thenReturn(Optional.empty());
        when(departmentPositionRepository.save(any(DepartmentPositionEntity.class)))
                .thenReturn(relationEntity);

        // Act
        DepartmentPositionRsp result = departmentPositionService.createDepartmentPosition(relationReq);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getDepartmentId()).isEqualTo(departmentId);
        assertThat(result.getPositionId()).isEqualTo(positionId);
        verify(departmentPositionRepository, times(1)).save(any(DepartmentPositionEntity.class));
    }

    @Test
    void createDepartmentPosition_shouldThrowExceptionWhenDepartmentNotFound() {
        // Arrange
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> departmentPositionService.createDepartmentPosition(relationReq))
                .isInstanceOf(ApiException.class)
                .hasMessage("部门不存在");
        verify(departmentPositionRepository, never()).save(any());
    }

    @Test
    void createDepartmentPosition_shouldThrowExceptionWhenPositionNotFound() {
        // Arrange
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(departmentEntity));
        when(positionRepository.findById(positionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> departmentPositionService.createDepartmentPosition(relationReq))
                .isInstanceOf(ApiException.class)
                .hasMessage("岗位不存在");
        verify(departmentPositionRepository, never()).save(any());
    }

    @Test
    void createDepartmentPosition_shouldThrowExceptionWhenRelationExists() {
        // Arrange
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(departmentEntity));
        when(positionRepository.findById(positionId)).thenReturn(Optional.of(positionEntity));
        when(departmentPositionRepository.findByDepartmentIdAndPositionId(departmentId, positionId))
                .thenReturn(Optional.of(relationEntity));

        // Act & Assert
        assertThatThrownBy(() -> departmentPositionService.createDepartmentPosition(relationReq))
                .isInstanceOf(ApiException.class)
                .hasMessage("该部门已配置此岗位");
        verify(departmentPositionRepository, never()).save(any());
    }

    @Test
    void deleteDepartmentPosition_shouldDeleteRelationWhenExists() {
        // Arrange
        when(departmentPositionRepository.findByDepartmentIdAndPositionId(departmentId, positionId))
                .thenReturn(Optional.of(relationEntity));

        // Act
        departmentPositionService.deleteDepartmentPosition(departmentId, positionId);

        // Assert
        verify(departmentPositionRepository, times(1))
                .findByDepartmentIdAndPositionId(departmentId, positionId);
        verify(departmentPositionRepository, times(1)).delete(relationEntity);
    }

    @Test
    void deleteDepartmentPosition_shouldThrowExceptionWhenRelationNotFound() {
        // Arrange
        when(departmentPositionRepository.findByDepartmentIdAndPositionId(departmentId, positionId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> departmentPositionService.deleteDepartmentPosition(departmentId, positionId))
                .isInstanceOf(ApiException.class)
                .hasMessage("部门岗位关联不存在");
        verify(departmentPositionRepository, never()).delete(any());
    }
}
