package com.reythecoder.organization.service;

import com.reythecoder.organization.dto.request.PersonnelCreateReq;
import com.reythecoder.organization.dto.request.PersonnelUpdateReq;
import com.reythecoder.organization.dto.response.PersonnelRsp;
import com.reythecoder.organization.entity.PersonnelEntity;
import com.reythecoder.organization.exception.ApiException;
import com.reythecoder.organization.repository.PersonnelRepository;
import com.reythecoder.organization.service.impl.PersonnelServiceImpl;
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
class PersonnelServiceTest {

    @Mock
    private PersonnelRepository personnelRepository;

    @InjectMocks
    private PersonnelServiceImpl personnelService;

    private UUID personnelId;
    private PersonnelEntity personnelEntity;
    private PersonnelRsp personnelRsp;
    private PersonnelCreateReq personnelCreateReq;
    private PersonnelUpdateReq personnelUpdateReq;

    @BeforeEach
    void setUp() {
        personnelId = UUIDv7.randomUUID();
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
                tenantId
        );

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
                tenantId
        );

        personnelCreateReq = new PersonnelCreateReq(
                "张三",
                "M",
                "110101199001011234",
                "13800138000",
                "010-12345678",
                "010-87654321",
                "zhangsan@example.com"
        );

        personnelUpdateReq = new PersonnelUpdateReq(
                "李四",
                "F",
                "110101199001015678",
                "13900139000",
                "010-87654321",
                "010-12345678",
                "lisi@example.com"
        );
    }

    @Test
    void getAllPersonnel_shouldReturnAllPersonnel() {
        // Arrange
        when(personnelRepository.findAll()).thenReturn(List.of(personnelEntity));

        // Act
        List<PersonnelRsp> result = personnelService.getAllPersonnel();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(personnelEntity.getId());
        assertThat(result.get(0).getName()).isEqualTo(personnelEntity.getName());
        verify(personnelRepository, times(1)).findAll();
    }

    @Test
    void getAllPersonnel_shouldReturnEmptyListWhenNoPersonnel() {
        // Arrange
        when(personnelRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<PersonnelRsp> result = personnelService.getAllPersonnel();

        // Assert
        assertThat(result).isEmpty();
        verify(personnelRepository, times(1)).findAll();
    }

    @Test
    void getPersonnelById_shouldReturnPersonnelWhenExists() {
        // Arrange
        when(personnelRepository.findById(personnelId)).thenReturn(Optional.of(personnelEntity));

        // Act
        PersonnelRsp result = personnelService.getPersonnelById(personnelId);

        // Assert
        assertThat(result.getId()).isEqualTo(personnelEntity.getId());
        assertThat(result.getName()).isEqualTo(personnelEntity.getName());
        verify(personnelRepository, times(1)).findById(personnelId);
    }

    @Test
    void getPersonnelById_shouldThrowExceptionWhenPersonnelNotFound() {
        // Arrange
        when(personnelRepository.findById(personnelId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> personnelService.getPersonnelById(personnelId))
                .isInstanceOf(ApiException.class)
                .hasMessage("人员不存在");
        verify(personnelRepository, times(1)).findById(personnelId);
    }

    @Test
    void createPersonnel_shouldReturnCreatedPersonnel() {
        // Arrange
        when(personnelRepository.save(any(PersonnelEntity.class))).thenReturn(personnelEntity);

        // Act
        PersonnelRsp result = personnelService.createPersonnel(personnelCreateReq);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo(personnelCreateReq.getName());
        verify(personnelRepository, times(1)).save(any(PersonnelEntity.class));
    }

    @Test
    void updatePersonnel_shouldReturnUpdatedPersonnelWhenExists() {
        // Arrange
        PersonnelEntity updatedEntity = new PersonnelEntity(
                personnelId,
                "李四",
                "F",
                "110101199001015678",
                "13900139000",
                "010-87654321",
                "010-12345678",
                "lisi@example.com",
                null,
                personnelEntity.getCreateTime(),
                OffsetDateTime.now(),
                personnelEntity.getTenantId()
        );

        when(personnelRepository.findById(personnelId)).thenReturn(Optional.of(personnelEntity));
        when(personnelRepository.save(personnelEntity)).thenReturn(updatedEntity);

        // Act
        PersonnelRsp result = personnelService.updatePersonnel(personnelId, personnelUpdateReq);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(updatedEntity.getId());
        assertThat(result.getName()).isEqualTo(updatedEntity.getName());
        assertThat(result.getIdCard()).isEqualTo(updatedEntity.getIdCard());
        verify(personnelRepository, times(1)).findById(personnelId);
        verify(personnelRepository, times(1)).save(personnelEntity);
    }

    @Test
    void updatePersonnel_shouldThrowExceptionWhenPersonnelNotFound() {
        // Arrange
        when(personnelRepository.findById(personnelId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> personnelService.updatePersonnel(personnelId, personnelUpdateReq))
                .isInstanceOf(ApiException.class)
                .hasMessage("人员不存在");
        verify(personnelRepository, times(1)).findById(personnelId);
        verify(personnelRepository, never()).save(any());
    }

    @Test
    void deletePersonnel_shouldDeletePersonnelWhenExists() {
        // Arrange
        when(personnelRepository.findById(personnelId)).thenReturn(Optional.of(personnelEntity));

        // Act
        personnelService.deletePersonnel(personnelId);

        // Assert
        verify(personnelRepository, times(1)).findById(personnelId);
        verify(personnelRepository, times(1)).delete(personnelEntity);
    }

    @Test
    void deletePersonnel_shouldThrowExceptionWhenPersonnelNotFound() {
        // Arrange
        when(personnelRepository.findById(personnelId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> personnelService.deletePersonnel(personnelId))
                .isInstanceOf(ApiException.class)
                .hasMessage("人员不存在");
        verify(personnelRepository, times(1)).findById(personnelId);
        verify(personnelRepository, never()).delete(any());
    }
}