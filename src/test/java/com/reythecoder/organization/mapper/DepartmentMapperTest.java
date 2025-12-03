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
        assertThat(entity.id()).isNotNull();
        assertThat(entity.name()).isEqualTo(createReq.name());
        assertThat(entity.englishName()).isEqualTo(createReq.englishName());
        assertThat(entity.shortName()).isEqualTo(createReq.shortName());
        assertThat(entity.orgCode()).isEqualTo(createReq.orgCode());
        assertThat(entity.phone()).isEqualTo(createReq.phone());
        assertThat(entity.fax()).isEqualTo(createReq.fax());
        assertThat(entity.email()).isEqualTo(createReq.email());
        assertThat(entity.address()).isEqualTo(createReq.address());
        assertThat(entity.postalCode()).isEqualTo(createReq.postalCode());
        assertThat(entity.createTime()).isNotNull();
        assertThat(entity.updateTime()).isNotNull();
        assertThat(entity.tenantId()).isNotNull();
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
        assertThat(rsp.id()).isEqualTo(entity.id());
        assertThat(rsp.name()).isEqualTo(entity.name());
        assertThat(rsp.englishName()).isEqualTo(entity.englishName());
        assertThat(rsp.shortName()).isEqualTo(entity.shortName());
        assertThat(rsp.orgCode()).isEqualTo(entity.orgCode());
        assertThat(rsp.phone()).isEqualTo(entity.phone());
        assertThat(rsp.fax()).isEqualTo(entity.fax());
        assertThat(rsp.email()).isEqualTo(entity.email());
        assertThat(rsp.address()).isEqualTo(entity.address());
        assertThat(rsp.postalCode()).isEqualTo(entity.postalCode());
        assertThat(rsp.createTime()).isEqualTo(entity.createTime());
        assertThat(rsp.updateTime()).isEqualTo(entity.updateTime());
        assertThat(rsp.tenantId()).isEqualTo(entity.tenantId());
    }
}
