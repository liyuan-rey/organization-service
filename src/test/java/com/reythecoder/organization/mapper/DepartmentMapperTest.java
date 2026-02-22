package com.reythecoder.organization.mapper;

import com.reythecoder.organization.dto.request.DepartmentCreateReq;
import com.reythecoder.organization.dto.response.DepartmentRsp;
import com.reythecoder.organization.entity.DepartmentEntity;

import io.github.robsonkades.uuidv7.UUIDv7;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DepartmentMapperTest {

    private DepartmentMapper departmentMapper;
    private UUID departmentId;
    private OffsetDateTime now;
    private UUID tenantId;

    @BeforeEach
    void setUp() {
        departmentMapper = DepartmentMapper.INSTANCE;
        departmentId = UUIDv7.randomUUID();
        now = OffsetDateTime.now();
        tenantId = UUIDv7.randomUUID();
    }

    @Test
    void toEntity_shouldMapCreateReqToEntity() {
        // Arrange
        DepartmentCreateReq createReq = new DepartmentCreateReq(
                "测试部门",
                "Test Department",
                "测试部",
                "TEST001",
                "123456789",
                "987654321",
                "test@example.com",
                "测试地址",
                "123456");

        // Act
        DepartmentEntity entity = departmentMapper.toEntity(createReq);

        // Assert
        assertThat(entity).isNotNull();
        // ID, createTime, updateTime, tenantId 由 Service 层设置，Mapper 不处理这些字段
        assertThat(entity.getName()).isEqualTo(createReq.name());
        assertThat(entity.getEnglishName()).isEqualTo(createReq.englishName());
        assertThat(entity.getShortName()).isEqualTo(createReq.shortName());
        assertThat(entity.getOrgCode()).isEqualTo(createReq.orgCode());
        assertThat(entity.getPhone()).isEqualTo(createReq.phone());
        assertThat(entity.getFax()).isEqualTo(createReq.fax());
        assertThat(entity.getEmail()).isEqualTo(createReq.email());
        assertThat(entity.getAddress()).isEqualTo(createReq.address());
        assertThat(entity.getPostalCode()).isEqualTo(createReq.postalCode());
    }

    @Test
    void toRsp_shouldMapEntityToRsp() {
        // Arrange
        DepartmentEntity entity = new DepartmentEntity(
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

        // Act
        DepartmentRsp rsp = departmentMapper.toRsp(entity);

        // Assert
        assertThat(rsp).isNotNull();
        assertThat(rsp.id()).isEqualTo(entity.getId());
        assertThat(rsp.name()).isEqualTo(entity.getName());
        assertThat(rsp.englishName()).isEqualTo(entity.getEnglishName());
        assertThat(rsp.shortName()).isEqualTo(entity.getShortName());
        assertThat(rsp.orgCode()).isEqualTo(entity.getOrgCode());
        assertThat(rsp.phone()).isEqualTo(entity.getPhone());
        assertThat(rsp.fax()).isEqualTo(entity.getFax());
        assertThat(rsp.email()).isEqualTo(entity.getEmail());
        assertThat(rsp.address()).isEqualTo(entity.getAddress());
        assertThat(rsp.postalCode()).isEqualTo(entity.getPostalCode());
        assertThat(rsp.createTime()).isEqualTo(entity.getCreateTime());
        assertThat(rsp.updateTime()).isEqualTo(entity.getUpdateTime());
        assertThat(rsp.tenantId()).isEqualTo(entity.getTenantId());
    }
}