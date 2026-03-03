package com.reythecoder.organization.service;

import com.reythecoder.organization.dto.request.DepartmentPersonnelCreateReq;
import com.reythecoder.organization.dto.response.DepartmentPersonnelRsp;
import com.reythecoder.organization.entity.DepartmentEntity;
import com.reythecoder.organization.entity.DepartmentPersonnelEntity;
import com.reythecoder.organization.entity.PersonnelEntity;
import com.reythecoder.organization.exception.ApiException;
import com.reythecoder.organization.repository.DepartmentPersonnelRepository;
import com.reythecoder.organization.repository.DepartmentRepository;
import com.reythecoder.organization.repository.PersonnelRepository;
import com.reythecoder.organization.service.impl.DepartmentPersonnelServiceImpl;

import io.github.robsonkades.uuidv7.UUIDv7;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentPersonnelServiceTest {

    @Mock
    private DepartmentPersonnelRepository departmentPersonnelRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private PersonnelRepository personnelRepository;

    @InjectMocks
    private DepartmentPersonnelServiceImpl departmentPersonnelService;

    private UUID departmentId;
    private UUID personnelId;
    private DepartmentPersonnelEntity entity;
    private DepartmentPersonnelCreateReq createReq;
    private DepartmentEntity department;
    private PersonnelEntity personnel;

    @BeforeEach
    void setUp() {
        departmentId = UUIDv7.randomUUID();
        personnelId = UUIDv7.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        entity = new DepartmentPersonnelEntity();
        entity.setId(UUIDv7.randomUUID());
        entity.setDepartmentId(departmentId);
        entity.setPersonnelId(personnelId);
        entity.setIsPrimary(true);
        entity.setPosition("经理");
        entity.setSortOrder(1);
        entity.setCreateTime(now);
        entity.setUpdateTime(now);

        createReq = new DepartmentPersonnelCreateReq(
                departmentId,
                personnelId,
                true,
                "经理",
                1);

        department = new DepartmentEntity();
        department.setId(departmentId);
        department.setName("技术部");

        personnel = new PersonnelEntity();
        personnel.setId(personnelId);
        personnel.setName("张三");
    }

    @Test
    void getByDepartmentId_shouldReturnPersonnelList() {
        when(departmentPersonnelRepository.findByDepartmentIdOrderBySortOrderAsc(departmentId)).thenReturn(List.of(entity));
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
        when(personnelRepository.findById(personnelId)).thenReturn(Optional.of(personnel));

        List<DepartmentPersonnelRsp> result = departmentPersonnelService.getByDepartmentId(departmentId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPersonnelId()).isEqualTo(personnelId);
        assertThat(result.get(0).getPersonnelName()).isEqualTo("张三");
        verify(departmentPersonnelRepository, times(1)).findByDepartmentIdOrderBySortOrderAsc(departmentId);
    }

    @Test
    void getByPersonnelId_shouldReturnDepartmentList() {
        when(departmentPersonnelRepository.findByPersonnelId(personnelId)).thenReturn(List.of(entity));
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
        when(personnelRepository.findById(personnelId)).thenReturn(Optional.of(personnel));

        List<DepartmentPersonnelRsp> result = departmentPersonnelService.getByPersonnelId(personnelId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDepartmentId()).isEqualTo(departmentId);
        assertThat(result.get(0).getDepartmentName()).isEqualTo("技术部");
        verify(departmentPersonnelRepository, times(1)).findByPersonnelId(personnelId);
    }

    @Test
    void create_shouldReturnCreatedEntity() {
        when(departmentPersonnelRepository.existsByDepartmentIdAndPersonnelId(departmentId, personnelId)).thenReturn(false);
        when(departmentPersonnelRepository.save(any(DepartmentPersonnelEntity.class))).thenReturn(entity);

        DepartmentPersonnelRsp result = departmentPersonnelService.create(createReq);

        assertThat(result).isNotNull();
        assertThat(result.getPersonnelId()).isEqualTo(personnelId);
        verify(departmentPersonnelRepository, times(1)).existsByDepartmentIdAndPersonnelId(departmentId, personnelId);
        verify(departmentPersonnelRepository, times(1)).save(any(DepartmentPersonnelEntity.class));
    }

    @Test
    void create_shouldThrowExceptionWhenRelationExists() {
        when(departmentPersonnelRepository.existsByDepartmentIdAndPersonnelId(departmentId, personnelId)).thenReturn(true);

        assertThatThrownBy(() -> departmentPersonnelService.create(createReq))
                .isInstanceOf(ApiException.class)
                .hasMessage("该人员已在此部门中");
        verify(departmentPersonnelRepository, times(1)).existsByDepartmentIdAndPersonnelId(departmentId, personnelId);
        verify(departmentPersonnelRepository, never()).save(any());
    }

    @Test
    void delete_shouldDeleteRelation() {
        doNothing().when(departmentPersonnelRepository).deleteByDepartmentIdAndPersonnelId(departmentId, personnelId);

        departmentPersonnelService.delete(departmentId, personnelId);

        verify(departmentPersonnelRepository, times(1)).deleteByDepartmentIdAndPersonnelId(departmentId, personnelId);
    }

    @Test
    void setPrimaryDepartment_shouldUpdatePrimaryFlag() {
        UUID otherDeptId = UUIDv7.randomUUID();
        DepartmentPersonnelEntity otherEntity = new DepartmentPersonnelEntity();
        otherEntity.setId(UUIDv7.randomUUID());
        otherEntity.setDepartmentId(otherDeptId);
        otherEntity.setPersonnelId(personnelId);
        otherEntity.setIsPrimary(true);

        when(departmentPersonnelRepository.findByPersonnelId(personnelId)).thenReturn(List.of(entity, otherEntity));
        when(departmentPersonnelRepository.saveAll(any())).thenReturn(List.of(entity, otherEntity));

        departmentPersonnelService.setPrimaryDepartment(personnelId, departmentId);

        verify(departmentPersonnelRepository, times(1)).findByPersonnelId(personnelId);
        verify(departmentPersonnelRepository, times(1)).saveAll(any());
    }
}