package com.reythecoder.organization.service;

import com.reythecoder.organization.dto.request.DepartmentCreateReq;
import com.reythecoder.organization.dto.request.DepartmentUpdateReq;
import com.reythecoder.organization.dto.response.DepartmentRsp;
import com.reythecoder.organization.entity.DepartmentEntity;
import com.reythecoder.organization.exception.ApiException;

import com.reythecoder.organization.repository.DepartmentRepository;
import com.reythecoder.organization.service.impl.DepartmentServiceImpl;

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
import static org.mockito.ArgumentMatchers.any;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private UUID departmentId;
    private DepartmentEntity departmentEntity;
    private DepartmentRsp departmentRsp;
    private DepartmentCreateReq departmentCreateReq;
    private DepartmentUpdateReq departmentUpdateReq;

    @BeforeEach
    void setUp() {
        departmentId = UUIDv7.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        UUID tenantId = UUIDv7.randomUUID();

        departmentEntity = new DepartmentEntity(
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
    void getAllDepartments_shouldReturnAllDepartments() {
        // Arrange
        when(departmentRepository.findAll()).thenReturn(List.of(departmentEntity));

        // Act
        List<DepartmentRsp> result = departmentService.getAllDepartments();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(departmentEntity.id());
        assertThat(result.get(0).name()).isEqualTo(departmentEntity.name());
        verify(departmentRepository, times(1)).findAll();
    }

    @Test
    void getAllDepartments_shouldReturnEmptyListWhenNoDepartments() {
        // Arrange
        when(departmentRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<DepartmentRsp> result = departmentService.getAllDepartments();

        // Assert
        assertThat(result).isEmpty();
        verify(departmentRepository, times(1)).findAll();
    }

    @Test
    void getDepartmentById_shouldReturnDepartmentWhenExists() {
        // Arrange
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(departmentEntity));

        // Act
        DepartmentRsp result = departmentService.getDepartmentById(departmentId);

        // Assert
        assertThat(result.id()).isEqualTo(departmentEntity.id());
        assertThat(result.name()).isEqualTo(departmentEntity.name());
        verify(departmentRepository, times(1)).findById(departmentId);
    }

    @Test
    void getDepartmentById_shouldThrowExceptionWhenDepartmentNotFound() {
        // Arrange
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> departmentService.getDepartmentById(departmentId))
                .isInstanceOf(ApiException.class)
                .hasMessage("部门不存在");
        verify(departmentRepository, times(1)).findById(departmentId);
    }

    @Test
    void createDepartment_shouldReturnCreatedDepartment() {
        // Arrange
        when(departmentRepository.save(any(DepartmentEntity.class))).thenReturn(departmentEntity);

        // Act
        DepartmentRsp result = departmentService.createDepartment(departmentCreateReq);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(departmentEntity.id());
        assertThat(result.name()).isEqualTo(departmentEntity.name());
        verify(departmentRepository, times(1)).save(any(DepartmentEntity.class));
    }

    @Test
    void updateDepartment_shouldReturnUpdatedDepartmentWhenExists() {
        // Arrange
        DepartmentEntity updatedEntity = new DepartmentEntity(
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
                departmentEntity.createTime(),
                OffsetDateTime.now(),
                departmentEntity.tenantId());

        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(departmentEntity));
        when(departmentRepository.save(departmentEntity)).thenReturn(updatedEntity);

        // Act
        DepartmentRsp result = departmentService.updateDepartment(departmentId, departmentUpdateReq);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(updatedEntity.id());
        assertThat(result.name()).isEqualTo(updatedEntity.name());
        assertThat(result.orgCode()).isEqualTo(updatedEntity.orgCode());
        verify(departmentRepository, times(1)).findById(departmentId);
        verify(departmentRepository, times(1)).save(departmentEntity);
    }

    @Test
    void updateDepartment_shouldThrowExceptionWhenDepartmentNotFound() {
        // Arrange
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> departmentService.updateDepartment(departmentId, departmentUpdateReq))
                .isInstanceOf(ApiException.class)
                .hasMessage("部门不存在");
        verify(departmentRepository, times(1)).findById(departmentId);
        verify(departmentRepository, never()).save(any());
    }

    @Test
    void deleteDepartment_shouldDeleteDepartmentWhenExists() {
        // Arrange
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(departmentEntity));

        // Act
        departmentService.deleteDepartment(departmentId);

        // Assert
        verify(departmentRepository, times(1)).findById(departmentId);
        verify(departmentRepository, times(1)).delete(departmentEntity);
    }

    @Test
    void deleteDepartment_shouldThrowExceptionWhenDepartmentNotFound() {
        // Arrange
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> departmentService.deleteDepartment(departmentId))
                .isInstanceOf(ApiException.class)
                .hasMessage("部门不存在");
        verify(departmentRepository, times(1)).findById(departmentId);
        verify(departmentRepository, never()).delete(any());
    }
}
