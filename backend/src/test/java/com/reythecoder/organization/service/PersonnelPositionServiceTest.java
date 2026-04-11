package com.reythecoder.organization.service;

import com.reythecoder.organization.dto.request.PersonnelPositionReq;
import com.reythecoder.organization.dto.response.PersonnelPositionRsp;
import com.reythecoder.organization.entity.DepartmentEntity;
import com.reythecoder.organization.entity.PersonnelEntity;
import com.reythecoder.organization.entity.PersonnelPositionEntity;
import com.reythecoder.organization.entity.PositionEntity;
import com.reythecoder.common.exception.ApiException;
import com.reythecoder.organization.repository.DepartmentRepository;
import com.reythecoder.organization.repository.PersonnelPositionRepository;
import com.reythecoder.organization.repository.PersonnelRepository;
import com.reythecoder.organization.repository.PositionRepository;
import com.reythecoder.organization.service.impl.PersonnelPositionServiceImpl;

import io.github.robsonkades.uuidv7.UUIDv7;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
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
class PersonnelPositionServiceTest {

    @Mock
    private PersonnelPositionRepository personnelPositionRepository;

    @Mock
    private PersonnelRepository personnelRepository;

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private PersonnelPositionServiceImpl personnelPositionService;

    private UUID personnelId;
    private UUID positionId;
    private UUID departmentId;
    private UUID relationId;
    private PersonnelEntity personnelEntity;
    private PositionEntity positionEntity;
    private DepartmentEntity departmentEntity;
    private PersonnelPositionEntity relationEntity;
    private PersonnelPositionReq relationReq;

    @BeforeEach
    void setUp() {
        personnelId = UUIDv7.randomUUID();
        positionId = UUIDv7.randomUUID();
        departmentId = UUIDv7.randomUUID();
        relationId = UUIDv7.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        UUID tenantId = UUIDv7.randomUUID();

        personnelEntity = new PersonnelEntity(
                personnelId,
                "张三",
                "M",
                "110101199001011234",
                "13800138000",
                "010-12345678",
                "010-87654321",
                "zhangsan@example.com",
                null,
                now,
                now,
                tenantId,
                false);

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
                tenantId,
                false);

        relationEntity = new PersonnelPositionEntity(
                relationId,
                personnelId,
                positionId,
                departmentId,
                true,
                LocalDate.now(),
                null,
                1,
                now,
                now,
                tenantId);

        relationReq = new PersonnelPositionReq(
                personnelId,
                positionId,
                departmentId,
                true,
                LocalDate.now(),
                null,
                1);
    }

    @Test
    void getAllPersonnelPositions_shouldReturnAllRelations() {
        // Arrange
        when(personnelPositionRepository.findAll()).thenReturn(List.of(relationEntity));
        when(personnelRepository.findById(personnelId)).thenReturn(Optional.of(personnelEntity));
        when(positionRepository.findById(positionId)).thenReturn(Optional.of(positionEntity));
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(departmentEntity));

        // Act
        List<PersonnelPositionRsp> result = personnelPositionService.getAllPersonnelPositions();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPersonnelId()).isEqualTo(personnelId);
        assertThat(result.get(0).getPositionId()).isEqualTo(positionId);
        verify(personnelPositionRepository, times(1)).findAll();
    }

    @Test
    void getPositionsByPersonnelId_shouldReturnPositionsForPersonnel() {
        // Arrange
        when(personnelPositionRepository.findByPersonnelId(personnelId))
                .thenReturn(List.of(relationEntity));
        when(personnelRepository.findById(personnelId)).thenReturn(Optional.of(personnelEntity));
        when(positionRepository.findById(positionId)).thenReturn(Optional.of(positionEntity));
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(departmentEntity));

        // Act
        List<PersonnelPositionRsp> result = personnelPositionService.getPositionsByPersonnelId(personnelId);

        // Assert
        assertThat(result).hasSize(1);
        verify(personnelPositionRepository, times(1)).findByPersonnelId(personnelId);
    }

    @Test
    void getPersonnelByPositionId_shouldReturnPersonnelForPosition() {
        // Arrange
        when(personnelPositionRepository.findByPositionId(positionId))
                .thenReturn(List.of(relationEntity));
        when(personnelRepository.findById(personnelId)).thenReturn(Optional.of(personnelEntity));
        when(positionRepository.findById(positionId)).thenReturn(Optional.of(positionEntity));
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(departmentEntity));

        // Act
        List<PersonnelPositionRsp> result = personnelPositionService.getPersonnelByPositionId(positionId);

        // Assert
        assertThat(result).hasSize(1);
        verify(personnelPositionRepository, times(1)).findByPositionId(positionId);
    }

    @Test
    void createPersonnelPosition_shouldReturnCreatedRelation() {
        // Arrange
        when(personnelRepository.findById(personnelId)).thenReturn(Optional.of(personnelEntity));
        when(positionRepository.findById(positionId)).thenReturn(Optional.of(positionEntity));
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(departmentEntity));
        when(personnelPositionRepository.findByPersonnelIdAndPositionIdAndDepartmentId(
                personnelId, positionId, departmentId)).thenReturn(Optional.empty());
        when(personnelPositionRepository.save(any(PersonnelPositionEntity.class)))
                .thenReturn(relationEntity);

        // Act
        PersonnelPositionRsp result = personnelPositionService.createPersonnelPosition(relationReq);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getPersonnelId()).isEqualTo(personnelId);
        assertThat(result.getPositionId()).isEqualTo(positionId);
        verify(personnelPositionRepository, times(1)).save(any(PersonnelPositionEntity.class));
    }

    @Test
    void createPersonnelPosition_shouldThrowExceptionWhenPersonnelNotFound() {
        // Arrange
        when(personnelRepository.findById(personnelId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> personnelPositionService.createPersonnelPosition(relationReq))
                .isInstanceOf(ApiException.class)
                .hasMessage("人员不存在");
        verify(personnelPositionRepository, never()).save(any());
    }

    @Test
    void createPersonnelPosition_shouldThrowExceptionWhenPositionNotFound() {
        // Arrange
        when(personnelRepository.findById(personnelId)).thenReturn(Optional.of(personnelEntity));
        when(positionRepository.findById(positionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> personnelPositionService.createPersonnelPosition(relationReq))
                .isInstanceOf(ApiException.class)
                .hasMessage("岗位不存在");
        verify(personnelPositionRepository, never()).save(any());
    }

    @Test
    void createPersonnelPosition_shouldThrowExceptionWhenDepartmentNotFound() {
        // Arrange
        when(personnelRepository.findById(personnelId)).thenReturn(Optional.of(personnelEntity));
        when(positionRepository.findById(positionId)).thenReturn(Optional.of(positionEntity));
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> personnelPositionService.createPersonnelPosition(relationReq))
                .isInstanceOf(ApiException.class)
                .hasMessage("部门不存在");
        verify(personnelPositionRepository, never()).save(any());
    }

    @Test
    void createPersonnelPosition_shouldThrowExceptionWhenRelationExists() {
        // Arrange
        when(personnelRepository.findById(personnelId)).thenReturn(Optional.of(personnelEntity));
        when(positionRepository.findById(positionId)).thenReturn(Optional.of(positionEntity));
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(departmentEntity));
        when(personnelPositionRepository.findByPersonnelIdAndPositionIdAndDepartmentId(
                personnelId, positionId, departmentId)).thenReturn(Optional.of(relationEntity));

        // Act & Assert
        assertThatThrownBy(() -> personnelPositionService.createPersonnelPosition(relationReq))
                .isInstanceOf(ApiException.class)
                .hasMessage("该人员已配置此岗位");
        verify(personnelPositionRepository, never()).save(any());
    }

    @Test
    void updatePersonnelPosition_shouldReturnUpdatedRelation() {
        // Arrange
        PersonnelPositionReq updateReq = new PersonnelPositionReq(
                personnelId, positionId, departmentId, false,
                LocalDate.now(), LocalDate.now(), 0);

        when(personnelPositionRepository.findById(relationId)).thenReturn(Optional.of(relationEntity));
        when(personnelPositionRepository.save(relationEntity)).thenReturn(relationEntity);

        // Act
        PersonnelPositionRsp result = personnelPositionService.updatePersonnelPosition(relationId, updateReq);

        // Assert
        assertThat(result).isNotNull();
        verify(personnelPositionRepository, times(1)).findById(relationId);
        verify(personnelPositionRepository, times(1)).save(relationEntity);
    }

    @Test
    void updatePersonnelPosition_shouldThrowExceptionWhenRelationNotFound() {
        // Arrange
        PersonnelPositionReq updateReq = new PersonnelPositionReq(
                personnelId, positionId, departmentId, false,
                LocalDate.now(), LocalDate.now(), 0);

        when(personnelPositionRepository.findById(relationId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> personnelPositionService.updatePersonnelPosition(relationId, updateReq))
                .isInstanceOf(ApiException.class)
                .hasMessage("人员岗位关联不存在");
        verify(personnelPositionRepository, never()).save(any());
    }

    @Test
    void deletePersonnelPosition_shouldDeleteRelationWhenExists() {
        // Arrange
        when(personnelPositionRepository.findById(relationId)).thenReturn(Optional.of(relationEntity));

        // Act
        personnelPositionService.deletePersonnelPosition(relationId);

        // Assert
        verify(personnelPositionRepository, times(1)).findById(relationId);
        verify(personnelPositionRepository, times(1)).delete(relationEntity);
    }

    @Test
    void deletePersonnelPosition_shouldThrowExceptionWhenRelationNotFound() {
        // Arrange
        when(personnelPositionRepository.findById(relationId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> personnelPositionService.deletePersonnelPosition(relationId))
                .isInstanceOf(ApiException.class)
                .hasMessage("人员岗位关联不存在");
        verify(personnelPositionRepository, never()).delete(any());
    }
}
