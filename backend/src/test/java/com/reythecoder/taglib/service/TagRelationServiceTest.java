package com.reythecoder.taglib.service;

import com.reythecoder.organization.exception.ApiException;
import com.reythecoder.taglib.dto.request.TagRelationQueryReq;
import com.reythecoder.taglib.dto.request.TagRelationReq;
import com.reythecoder.taglib.dto.response.TagRelationRsp;
import com.reythecoder.taglib.entity.TagEntity;
import com.reythecoder.taglib.entity.TagRelationEntity;
import com.reythecoder.taglib.repository.TagRelationRepository;
import com.reythecoder.taglib.repository.TagRepository;
import com.reythecoder.taglib.service.impl.TagRelationServiceImpl;

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
class TagRelationServiceTest {

    @Mock
    private TagRelationRepository tagRelationRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagRelationServiceImpl tagRelationService;

    private UUID relationId;
    private UUID tagId;
    private UUID objectId;
    private TagRelationEntity tagRelationEntity;
    private TagEntity tagEntity;

    @BeforeEach
    void setUp() {
        relationId = UUIDv7.randomUUID();
        tagId = UUIDv7.randomUUID();
        objectId = UUIDv7.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        tagRelationEntity = new TagRelationEntity("DEPARTMENT", objectId, tagId);
        tagRelationEntity.setId(relationId);
        tagRelationEntity.setCreateTime(now);
        tagRelationEntity.setUpdateTime(now);

        tagEntity = new TagEntity("测试标签", UUIDv7.randomUUID(), null, "a0");
        tagEntity.setId(tagId);
        tagEntity.setCreateTime(now);
        tagEntity.setUpdateTime(now);
    }

    @Test
    void getByObject_shouldReturnRelations() {
        // Arrange
        when(tagRelationRepository.findByObjectTypeAndObjectId("DEPARTMENT", objectId))
                .thenReturn(List.of(tagRelationEntity));
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tagEntity));

        // Act
        List<TagRelationRsp> result = tagRelationService.getByObject("DEPARTMENT", objectId);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(relationId);
        assertThat(result.get(0).getObjectType()).isEqualTo("DEPARTMENT");
        assertThat(result.get(0).getObjectId()).isEqualTo(objectId);
        verify(tagRelationRepository, times(1)).findByObjectTypeAndObjectId("DEPARTMENT", objectId);
    }

    @Test
    void getByTag_shouldReturnRelations() {
        // Arrange
        when(tagRelationRepository.findByTagId(tagId)).thenReturn(List.of(tagRelationEntity));
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tagEntity));

        // Act
        List<TagRelationRsp> result = tagRelationService.getByTag(tagId);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTagId()).isEqualTo(tagId);
        verify(tagRelationRepository, times(1)).findByTagId(tagId);
    }

    @Test
    void batchCreate_shouldCreateRelations() {
        // Arrange
        TagRelationReq req = new TagRelationReq("DEPARTMENT", objectId, List.of(tagId));

        when(tagRepository.findByIdAndRemovedFalse(tagId)).thenReturn(Optional.of(tagEntity));
        when(tagRelationRepository.existsByObjectTypeAndObjectIdAndTagId("DEPARTMENT", objectId, tagId)).thenReturn(false);
        when(tagRelationRepository.save(any(TagRelationEntity.class))).thenReturn(tagRelationEntity);

        // Act
        List<TagRelationRsp> result = tagRelationService.batchCreate(req);

        // Assert
        assertThat(result).hasSize(1);
        verify(tagRepository, times(1)).findByIdAndRemovedFalse(tagId);
        verify(tagRelationRepository, times(1)).existsByObjectTypeAndObjectIdAndTagId("DEPARTMENT", objectId, tagId);
        verify(tagRelationRepository, times(1)).save(any(TagRelationEntity.class));
    }

    @Test
    void batchCreate_shouldSkipDuplicates() {
        // Arrange
        UUID tagId2 = UUIDv7.randomUUID();
        TagRelationReq req = new TagRelationReq("DEPARTMENT", objectId, List.of(tagId, tagId2));

        TagEntity tagEntity2 = new TagEntity("测试标签2", UUIDv7.randomUUID(), null, "a1");
        tagEntity2.setId(tagId2);

        when(tagRepository.findByIdAndRemovedFalse(tagId)).thenReturn(Optional.of(tagEntity));
        when(tagRepository.findByIdAndRemovedFalse(tagId2)).thenReturn(Optional.of(tagEntity2));
        when(tagRelationRepository.existsByObjectTypeAndObjectIdAndTagId("DEPARTMENT", objectId, tagId)).thenReturn(true);
        when(tagRelationRepository.existsByObjectTypeAndObjectIdAndTagId("DEPARTMENT", objectId, tagId2)).thenReturn(false);
        when(tagRelationRepository.save(any(TagRelationEntity.class))).thenReturn(tagRelationEntity);

        // Act
        List<TagRelationRsp> result = tagRelationService.batchCreate(req);

        // Assert
        assertThat(result).hasSize(1);
        verify(tagRelationRepository, times(1)).save(any(TagRelationEntity.class));
    }

    @Test
    void batchCreate_shouldThrowWhenTagNotFound() {
        // Arrange
        TagRelationReq req = new TagRelationReq("DEPARTMENT", objectId, List.of(tagId));

        when(tagRepository.findByIdAndRemovedFalse(tagId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> tagRelationService.batchCreate(req))
                .isInstanceOf(ApiException.class)
                .hasMessage("标签不存在: " + tagId);
        verify(tagRelationRepository, never()).save(any());
    }

    @Test
    void delete_shouldDeleteRelation() {
        // Arrange
        when(tagRelationRepository.findById(relationId)).thenReturn(Optional.of(tagRelationEntity));

        // Act
        tagRelationService.delete(relationId);

        // Assert
        verify(tagRelationRepository, times(1)).findById(relationId);
        verify(tagRelationRepository, times(1)).delete(tagRelationEntity);
    }

    @Test
    void delete_shouldThrowWhenNotFound() {
        // Arrange
        when(tagRelationRepository.findById(relationId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> tagRelationService.delete(relationId))
                .isInstanceOf(ApiException.class)
                .hasMessage("标签关联不存在");
        verify(tagRelationRepository, times(1)).findById(relationId);
        verify(tagRelationRepository, never()).delete(any());
    }

    @Test
    void queryByMultipleTags_shouldReturnMatchingObjects() {
        // Arrange
        TagRelationQueryReq req = new TagRelationQueryReq("DEPARTMENT", List.of(tagId));

        when(tagRelationRepository.findByObjectTypeAndTagIdsWithAllMatch("DEPARTMENT", List.of(tagId), 1L))
                .thenReturn(List.of(tagRelationEntity));
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tagEntity));

        // Act
        List<TagRelationRsp> result = tagRelationService.queryByMultipleTags(req);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getObjectId()).isEqualTo(objectId);
        verify(tagRelationRepository, times(1))
                .findByObjectTypeAndTagIdsWithAllMatch("DEPARTMENT", List.of(tagId), 1L);
    }
}
