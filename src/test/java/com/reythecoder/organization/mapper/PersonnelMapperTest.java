package com.reythecoder.organization.mapper;

import com.reythecoder.organization.dto.request.PersonnelCreateReq;
import com.reythecoder.organization.dto.response.PersonnelRsp;
import com.reythecoder.organization.entity.PersonnelEntity;
import io.github.robsonkades.uuidv7.UUIDv7;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PersonnelMapperTest {

    private PersonnelMapper personnelMapper;
    private UUID personnelId;
    private OffsetDateTime now;
    private UUID tenantId;

    @BeforeEach
    void setUp() {
        personnelMapper = PersonnelMapper.INSTANCE;
        personnelId = UUIDv7.randomUUID();
        now = OffsetDateTime.now();
        tenantId = UUIDv7.randomUUID();
    }

    @Test
    void toEntity_shouldMapCreateReqToEntity() {
        // Arrange
        PersonnelCreateReq createReq = new PersonnelCreateReq(
                "张三",
                "M",
                "110101199001011234",
                "13800138000",
                "010-12345678",
                "010-87654321",
                "zhangsan@example.com"
        );

        // Act
        PersonnelEntity entity = personnelMapper.toEntity(createReq);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo(createReq.getName());
        assertThat(entity.getGender()).isEqualTo(createReq.getGender());
        assertThat(entity.getIdCard()).isEqualTo(createReq.getIdCard());
        assertThat(entity.getMobile()).isEqualTo(createReq.getMobile());
        assertThat(entity.getTelephone()).isEqualTo(createReq.getTelephone());
        assertThat(entity.getFax()).isEqualTo(createReq.getFax());
        assertThat(entity.getEmail()).isEqualTo(createReq.getEmail());
        // Note: ID, createTime, updateTime, tenantId, photo are handled by service layer
    }

    @Test
    void toRsp_shouldMapEntityToRsp() {
        // Arrange
        PersonnelEntity entity = new PersonnelEntity(
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

        // Act
        PersonnelRsp rsp = personnelMapper.toRsp(entity);

        // Assert
        assertThat(rsp).isNotNull();
        assertThat(rsp.getId()).isEqualTo(entity.getId());
        assertThat(rsp.getName()).isEqualTo(entity.getName());
        assertThat(rsp.getGender()).isEqualTo(entity.getGender());
        assertThat(rsp.getIdCard()).isEqualTo(entity.getIdCard());
        assertThat(rsp.getMobile()).isEqualTo(entity.getMobile());
        assertThat(rsp.getTelephone()).isEqualTo(entity.getTelephone());
        assertThat(rsp.getFax()).isEqualTo(entity.getFax());
        assertThat(rsp.getEmail()).isEqualTo(entity.getEmail());
        assertThat(rsp.getCreateTime()).isEqualTo(entity.getCreateTime());
        assertThat(rsp.getUpdateTime()).isEqualTo(entity.getUpdateTime());
        assertThat(rsp.getTenantId()).isEqualTo(entity.getTenantId());
    }
}