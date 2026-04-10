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
        assertThat(entity.getName()).isEqualTo(createReq.getName());
        assertThat(entity.getEnglishName()).isEqualTo(createReq.getEnglishName());
        assertThat(entity.getShortName()).isEqualTo(createReq.getShortName());
        assertThat(entity.getOrgCode()).isEqualTo(createReq.getOrgCode());
        assertThat(entity.getPhone()).isEqualTo(createReq.getPhone());
        assertThat(entity.getFax()).isEqualTo(createReq.getFax());
        assertThat(entity.getEmail()).isEqualTo(createReq.getEmail());
        assertThat(entity.getAddress()).isEqualTo(createReq.getAddress());
        assertThat(entity.getPostalCode()).isEqualTo(createReq.getPostalCode());
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
                tenantId,
                false);

        // Act
        DepartmentRsp rsp = departmentMapper.toRsp(entity);

        // Assert
        assertThat(rsp).isNotNull();
        assertThat(rsp.getId()).isEqualTo(entity.getId());
        assertThat(rsp.getName()).isEqualTo(entity.getName());
        assertThat(rsp.getEnglishName()).isEqualTo(entity.getEnglishName());
        assertThat(rsp.getShortName()).isEqualTo(entity.getShortName());
        assertThat(rsp.getOrgCode()).isEqualTo(entity.getOrgCode());
        assertThat(rsp.getPhone()).isEqualTo(entity.getPhone());
        assertThat(rsp.getFax()).isEqualTo(entity.getFax());
        assertThat(rsp.getEmail()).isEqualTo(entity.getEmail());
        assertThat(rsp.getAddress()).isEqualTo(entity.getAddress());
        assertThat(rsp.getPostalCode()).isEqualTo(entity.getPostalCode());
        assertThat(rsp.getCreateTime()).isEqualTo(entity.getCreateTime());
        assertThat(rsp.getUpdateTime()).isEqualTo(entity.getUpdateTime());
        assertThat(rsp.getTenantId()).isEqualTo(entity.getTenantId());
    }
}