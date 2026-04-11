package com.reythecoder.taglib.service;

import com.reythecoder.organization.exception.ApiException;
import com.reythecoder.taglib.dto.request.TagCategoryCreateReq;
import com.reythecoder.taglib.dto.request.TagCategoryUpdateReq;
import com.reythecoder.taglib.dto.response.TagCategoryRsp;
import com.reythecoder.taglib.entity.TagCategoryEntity;
import com.reythecoder.taglib.repository.TagCategoryRepository;
import com.reythecoder.taglib.service.impl.TagCategoryServiceImpl;

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
class TagCategoryServiceTest {

    @Mock
    private TagCategoryRepository tagCategoryRepository;

    @InjectMocks
    private TagCategoryServiceImpl tagCategoryService;

    private UUID categoryId;
    private TagCategoryEntity tagCategoryEntity;
    private TagCategoryCreateReq createReq;
    private TagCategoryUpdateReq updateReq;

    @BeforeEach
    void setUp() {
        categoryId = UUIDv7.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        tagCategoryEntity = new TagCategoryEntity(
                "测试分类",
                "测试分类描述",
                "a0");
        // Set fields that the constructor auto-generates
        tagCategoryEntity.setId(categoryId);
        tagCategoryEntity.setCreateTime(now);
        tagCategoryEntity.setUpdateTime(now);

        createReq = new TagCategoryCreateReq("测试分类", "测试分类描述", null);
        updateReq = new TagCategoryUpdateReq("更新分类", "更新描述", "b0");
    }

    @Test
    void getAllCategories_shouldReturnActiveCategories() {
        // Arrange
        when(tagCategoryRepository.findByRemovedFalseOrderBySortRankAsc()).thenReturn(List.of(tagCategoryEntity));

        // Act
        List<TagCategoryRsp> result = tagCategoryService.getAllCategories();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(categoryId);
        assertThat(result.get(0).getName()).isEqualTo("测试分类");
        verify(tagCategoryRepository, times(1)).findByRemovedFalseOrderBySortRankAsc();
    }

    @Test
    void getAllCategories_shouldReturnEmptyList() {
        // Arrange
        when(tagCategoryRepository.findByRemovedFalseOrderBySortRankAsc()).thenReturn(Collections.emptyList());

        // Act
        List<TagCategoryRsp> result = tagCategoryService.getAllCategories();

        // Assert
        assertThat(result).isEmpty();
        verify(tagCategoryRepository, times(1)).findByRemovedFalseOrderBySortRankAsc();
    }

    @Test
    void getById_shouldReturnCategoryWhenExists() {
        // Arrange
        when(tagCategoryRepository.findById(categoryId)).thenReturn(Optional.of(tagCategoryEntity));

        // Act
        TagCategoryRsp result = tagCategoryService.getById(categoryId);

        // Assert
        assertThat(result.getId()).isEqualTo(categoryId);
        assertThat(result.getName()).isEqualTo("测试分类");
        verify(tagCategoryRepository, times(1)).findById(categoryId);
    }

    @Test
    void getById_shouldThrowWhenNotFound() {
        // Arrange
        when(tagCategoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> tagCategoryService.getById(categoryId))
                .isInstanceOf(ApiException.class)
                .hasMessage("标签分类不存在");
        verify(tagCategoryRepository, times(1)).findById(categoryId);
    }

    @Test
    void getById_shouldThrowWhenRemoved() {
        // Arrange
        tagCategoryEntity.setRemoved(true);
        when(tagCategoryRepository.findById(categoryId)).thenReturn(Optional.of(tagCategoryEntity));

        // Act & Assert
        assertThatThrownBy(() -> tagCategoryService.getById(categoryId))
                .isInstanceOf(ApiException.class)
                .hasMessage("标签分类不存在");
        verify(tagCategoryRepository, times(1)).findById(categoryId);
    }

    @Test
    void create_shouldReturnCreatedCategory() {
        // Arrange
        when(tagCategoryRepository.existsByNameAndRemovedFalse("测试分类")).thenReturn(false);
        when(tagCategoryRepository.findByRemovedFalseOrderBySortRankAsc()).thenReturn(Collections.emptyList());
        when(tagCategoryRepository.save(any(TagCategoryEntity.class))).thenReturn(tagCategoryEntity);

        // Act
        TagCategoryRsp result = tagCategoryService.create(createReq);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("测试分类");
        verify(tagCategoryRepository, times(1)).existsByNameAndRemovedFalse("测试分类");
        verify(tagCategoryRepository, times(1)).save(any(TagCategoryEntity.class));
    }

    @Test
    void create_shouldThrowWhenNameExists() {
        // Arrange
        when(tagCategoryRepository.existsByNameAndRemovedFalse("测试分类")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> tagCategoryService.create(createReq))
                .isInstanceOf(ApiException.class)
                .hasMessage("标签分类名称已存在");
        verify(tagCategoryRepository, times(1)).existsByNameAndRemovedFalse("测试分类");
        verify(tagCategoryRepository, never()).save(any());
    }

    @Test
    void update_shouldReturnUpdatedCategory() {
        // Arrange
        when(tagCategoryRepository.findById(categoryId)).thenReturn(Optional.of(tagCategoryEntity));
        when(tagCategoryRepository.save(tagCategoryEntity)).thenReturn(tagCategoryEntity);

        // Act
        TagCategoryRsp result = tagCategoryService.update(categoryId, updateReq);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(categoryId);
        verify(tagCategoryRepository, times(1)).findById(categoryId);
        verify(tagCategoryRepository, times(1)).save(tagCategoryEntity);
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        // Arrange
        when(tagCategoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> tagCategoryService.update(categoryId, updateReq))
                .isInstanceOf(ApiException.class)
                .hasMessage("标签分类不存在");
        verify(tagCategoryRepository, times(1)).findById(categoryId);
        verify(tagCategoryRepository, never()).save(any());
    }

    @Test
    void delete_shouldMarkAsRemoved() {
        // Arrange
        when(tagCategoryRepository.findById(categoryId)).thenReturn(Optional.of(tagCategoryEntity));
        when(tagCategoryRepository.save(tagCategoryEntity)).thenReturn(tagCategoryEntity);

        // Act
        tagCategoryService.delete(categoryId);

        // Assert
        assertThat(tagCategoryEntity.isRemoved()).isTrue();
        verify(tagCategoryRepository, times(1)).findById(categoryId);
        verify(tagCategoryRepository, times(1)).save(tagCategoryEntity);
    }

    @Test
    void delete_shouldThrowWhenNotFound() {
        // Arrange
        when(tagCategoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> tagCategoryService.delete(categoryId))
                .isInstanceOf(ApiException.class)
                .hasMessage("标签分类不存在");
        verify(tagCategoryRepository, times(1)).findById(categoryId);
        verify(tagCategoryRepository, never()).save(any());
    }
}
