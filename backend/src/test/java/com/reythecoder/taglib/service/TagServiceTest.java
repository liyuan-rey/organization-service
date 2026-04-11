package com.reythecoder.taglib.service;

import com.reythecoder.organization.exception.ApiException;
import com.reythecoder.taglib.dto.request.TagCreateReq;
import com.reythecoder.taglib.dto.request.TagUpdateReq;
import com.reythecoder.taglib.dto.response.TagRsp;
import com.reythecoder.taglib.dto.response.TagTreeRsp;
import com.reythecoder.taglib.entity.TagCategoryEntity;
import com.reythecoder.taglib.entity.TagEntity;
import com.reythecoder.taglib.repository.TagCategoryRepository;
import com.reythecoder.taglib.repository.TagRepository;
import com.reythecoder.taglib.service.impl.TagServiceImpl;

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
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private TagCategoryRepository tagCategoryRepository;

    @InjectMocks
    private TagServiceImpl tagService;

    private UUID categoryId;
    private TagCategoryEntity categoryEntity;
    private TagEntity parentTagEntity;
    private TagEntity childTagEntity;
    private TagCreateReq createReq;
    private TagCreateReq createChildReq;
    private TagUpdateReq updateReq;

    @BeforeEach
    void setUp() {
        categoryId = UUIDv7.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        categoryEntity = new TagCategoryEntity("测试分类", "测试分类描述", "a0");
        categoryEntity.setId(categoryId);
        categoryEntity.setCreateTime(now);
        categoryEntity.setUpdateTime(now);

        parentTagEntity = new TagEntity("父标签", categoryId, null, "a0");
        parentTagEntity.setId(UUIDv7.randomUUID());
        parentTagEntity.setCreateTime(now);
        parentTagEntity.setUpdateTime(now);

        childTagEntity = new TagEntity("子标签", categoryId, parentTagEntity.getId(), "a0");
        childTagEntity.setId(UUIDv7.randomUUID());
        childTagEntity.setCreateTime(now);
        childTagEntity.setUpdateTime(now);

        createReq = new TagCreateReq("父标签", categoryId, null, null);
        createChildReq = new TagCreateReq("子标签", categoryId, parentTagEntity.getId(), null);
        updateReq = new TagUpdateReq("更新标签", null, "b0");
    }

    @Test
    void getTagTreeByCategory_shouldReturnTree() {
        // Arrange
        when(tagCategoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryEntity));
        when(tagRepository.findByCategoryIdAndRemovedFalseOrderBySortRankAsc(categoryId))
                .thenReturn(List.of(parentTagEntity, childTagEntity));

        // Act
        List<TagTreeRsp> result = tagService.getTagTreeByCategory(categoryId);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("父标签");
        assertThat(result.get(0).getChildren()).hasSize(1);
        assertThat(result.get(0).getChildren().get(0).getName()).isEqualTo("子标签");
        verify(tagRepository, times(1)).findByCategoryIdAndRemovedFalseOrderBySortRankAsc(categoryId);
    }

    @Test
    void getTagTreeByCategory_shouldThrowWhenCategoryNotFound() {
        // Arrange
        when(tagCategoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> tagService.getTagTreeByCategory(categoryId))
                .isInstanceOf(ApiException.class)
                .hasMessage("标签分类不存在");
        verify(tagRepository, never()).findByCategoryIdAndRemovedFalseOrderBySortRankAsc(any());
    }

    @Test
    void getById_shouldReturnTag() {
        // Arrange
        when(tagRepository.findByIdAndRemovedFalse(parentTagEntity.getId())).thenReturn(Optional.of(parentTagEntity));
        when(tagCategoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryEntity));

        // Act
        TagRsp result = tagService.getById(parentTagEntity.getId());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(parentTagEntity.getId());
        assertThat(result.getName()).isEqualTo("父标签");
        verify(tagRepository, times(1)).findByIdAndRemovedFalse(parentTagEntity.getId());
    }

    @Test
    void getById_shouldThrowWhenNotFound() {
        // Arrange
        UUID unknownId = UUIDv7.randomUUID();
        when(tagRepository.findByIdAndRemovedFalse(unknownId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> tagService.getById(unknownId))
                .isInstanceOf(ApiException.class)
                .hasMessage("标签不存在");
        verify(tagRepository, times(1)).findByIdAndRemovedFalse(unknownId);
    }

    @Test
    void create_shouldCreateRootTag() {
        // Arrange
        when(tagCategoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryEntity));
        when(tagRepository.existsByCategoryIdAndNameAndRemovedFalse(categoryId, "父标签")).thenReturn(false);
        when(tagRepository.findByCategoryIdAndRemovedFalseOrderBySortRankAsc(categoryId))
                .thenReturn(Collections.emptyList());
        when(tagRepository.save(any(TagEntity.class))).thenReturn(parentTagEntity);

        // Act
        TagRsp result = tagService.create(createReq);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("父标签");
        verify(tagRepository, times(1)).existsByCategoryIdAndNameAndRemovedFalse(categoryId, "父标签");
        verify(tagRepository, times(1)).save(any(TagEntity.class));
    }

    @Test
    void create_shouldCreateChildTag() {
        // Arrange
        when(tagCategoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryEntity));
        when(tagRepository.findByIdAndRemovedFalse(parentTagEntity.getId())).thenReturn(Optional.of(parentTagEntity));
        when(tagRepository.existsByCategoryIdAndNameAndRemovedFalse(categoryId, "子标签")).thenReturn(false);
        when(tagRepository.findByParentIdAndRemovedFalseOrderBySortRankAsc(parentTagEntity.getId()))
                .thenReturn(Collections.emptyList());
        when(tagRepository.save(any(TagEntity.class))).thenReturn(childTagEntity);

        // Act
        TagRsp result = tagService.create(createChildReq);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("子标签");
        verify(tagRepository, times(1)).findByIdAndRemovedFalse(parentTagEntity.getId());
        verify(tagRepository, times(1)).save(any(TagEntity.class));
    }

    @Test
    void create_shouldThrowWhenDuplicateName() {
        // Arrange
        when(tagCategoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryEntity));
        when(tagRepository.existsByCategoryIdAndNameAndRemovedFalse(categoryId, "父标签")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> tagService.create(createReq))
                .isInstanceOf(ApiException.class)
                .hasMessage("标签名称已存在");
        verify(tagRepository, times(1)).existsByCategoryIdAndNameAndRemovedFalse(categoryId, "父标签");
        verify(tagRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowWhenCategoryNotFound() {
        // Arrange
        when(tagCategoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> tagService.create(createReq))
                .isInstanceOf(ApiException.class)
                .hasMessage("标签分类不存在");
        verify(tagRepository, never()).save(any());
    }

    @Test
    void update_shouldUpdateTag() {
        // Arrange
        when(tagRepository.findByIdAndRemovedFalse(parentTagEntity.getId())).thenReturn(Optional.of(parentTagEntity));
        when(tagRepository.save(parentTagEntity)).thenReturn(parentTagEntity);
        when(tagCategoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryEntity));

        // Act
        TagRsp result = tagService.update(parentTagEntity.getId(), updateReq);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(parentTagEntity.getId());
        verify(tagRepository, times(1)).findByIdAndRemovedFalse(parentTagEntity.getId());
        verify(tagRepository, times(1)).save(parentTagEntity);
    }

    @Test
    void delete_shouldMarkRemovedRecursively() {
        // Arrange
        when(tagRepository.findByIdAndRemovedFalse(parentTagEntity.getId())).thenReturn(Optional.of(parentTagEntity));
        when(tagRepository.findByParentIdAndRemovedFalse(parentTagEntity.getId()))
                .thenReturn(List.of(childTagEntity));
        when(tagRepository.findByParentIdAndRemovedFalse(childTagEntity.getId()))
                .thenReturn(Collections.emptyList());
        when(tagRepository.save(any(TagEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        tagService.delete(parentTagEntity.getId());

        // Assert
        assertThat(parentTagEntity.isRemoved()).isTrue();
        assertThat(childTagEntity.isRemoved()).isTrue();
        verify(tagRepository, times(2)).save(any(TagEntity.class));
    }

    @Test
    void delete_shouldThrowWhenNotFound() {
        // Arrange
        UUID unknownId = UUIDv7.randomUUID();
        when(tagRepository.findByIdAndRemovedFalse(unknownId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> tagService.delete(unknownId))
                .isInstanceOf(ApiException.class)
                .hasMessage("标签不存在");
        verify(tagRepository, times(1)).findByIdAndRemovedFalse(unknownId);
        verify(tagRepository, never()).save(any());
    }
}
