package com.reythecoder.taglib.service.impl;

import com.reythecoder.common.utils.LexoRankUtils;
import com.reythecoder.common.exception.ApiException;
import com.reythecoder.taglib.dto.request.TagCreateReq;
import com.reythecoder.taglib.dto.request.TagUpdateReq;
import com.reythecoder.taglib.dto.response.TagRsp;
import com.reythecoder.taglib.dto.response.TagTreeRsp;
import com.reythecoder.taglib.entity.TagCategoryEntity;
import com.reythecoder.taglib.entity.TagEntity;
import com.reythecoder.taglib.mapper.TagMapper;
import com.reythecoder.taglib.repository.TagCategoryRepository;
import com.reythecoder.taglib.repository.TagRepository;
import com.reythecoder.taglib.service.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Validated
public class TagServiceImpl implements TagService {

    private static final Logger logger = LoggerFactory.getLogger(TagServiceImpl.class);
    private static final UUID ROOT_KEY = new UUID(0, 0);

    private final TagRepository tagRepository;
    private final TagCategoryRepository tagCategoryRepository;

    public TagServiceImpl(TagRepository tagRepository, TagCategoryRepository tagCategoryRepository) {
        this.tagRepository = tagRepository;
        this.tagCategoryRepository = tagCategoryRepository;
    }

    @Override
    public List<TagTreeRsp> getTagTreeByCategory(UUID categoryId) {
        logger.info("获取标签树, categoryId: {}", categoryId);
        validateCategoryExists(categoryId);

        List<TagEntity> tags = tagRepository.findByCategoryIdAndRemovedFalseOrderBySortRankAsc(categoryId);
        return buildTree(tags, categoryId);
    }

    @Override
    public TagRsp getById(UUID id) {
        logger.info("根据ID获取标签, id: {}", id);
        TagEntity entity = tagRepository.findByIdAndRemovedFalse(id)
                .orElseThrow(() -> new ApiException(404, "标签不存在"));
        return enrichTagRsp(entity);
    }

    @Override
    public TagRsp create(TagCreateReq req) {
        logger.info("创建标签, name: {}, categoryId: {}", req.getName(), req.getCategoryId());

        // Validate category exists and not removed
        validateCategoryExists(req.getCategoryId());

        // Validate parent tag if parentId is provided
        if (req.getParentId() != null) {
            tagRepository.findByIdAndRemovedFalse(req.getParentId())
                    .orElseThrow(() -> new ApiException(400, "父标签不存在"));
        }

        // Check duplicate name
        if (tagRepository.existsByCategoryIdAndNameAndRemovedFalse(req.getCategoryId(), req.getName())) {
            throw new ApiException(400, "标签名称已存在");
        }

        // Auto-generate sortRank if not provided
        String sortRank = req.getSortRank();
        if (sortRank == null || sortRank.isBlank()) {
            sortRank = generateSortRank(req.getCategoryId(), req.getParentId());
        }

        TagEntity entity = new TagEntity(req.getName(), req.getCategoryId(), req.getParentId(), sortRank);
        TagEntity savedEntity = tagRepository.save(entity);
        return enrichTagRsp(savedEntity);
    }

    @Override
    public TagRsp update(UUID id, TagUpdateReq req) {
        logger.info("更新标签, id: {}", id);
        TagEntity entity = tagRepository.findByIdAndRemovedFalse(id)
                .orElseThrow(() -> new ApiException(404, "标签不存在"));

        TagMapper.INSTANCE.updateEntity(req, entity);
        TagEntity savedEntity = tagRepository.save(entity);
        return enrichTagRsp(savedEntity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        logger.info("删除标签, id: {}", id);
        TagEntity entity = tagRepository.findByIdAndRemovedFalse(id)
                .orElseThrow(() -> new ApiException(404, "标签不存在"));
        recursiveDelete(entity);
    }

    private void recursiveDelete(TagEntity entity) {
        entity.setRemoved(true);
        tagRepository.save(entity);

        List<TagEntity> children = tagRepository.findByParentIdAndRemovedFalse(entity.getId());
        for (TagEntity child : children) {
            recursiveDelete(child);
        }
    }

    private void validateCategoryExists(UUID categoryId) {
        TagCategoryEntity category = tagCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ApiException(404, "标签分类不存在"));
        if (category.isRemoved()) {
            throw new ApiException(404, "标签分类不存在");
        }
    }

    private String generateSortRank(UUID categoryId, UUID parentId) {
        List<TagEntity> siblings;
        if (parentId != null) {
            siblings = tagRepository.findByParentIdAndRemovedFalseOrderBySortRankAsc(parentId);
        } else {
            // Root-level tags in category with no parent
            List<TagEntity> allTags = tagRepository.findByCategoryIdAndRemovedFalseOrderBySortRankAsc(categoryId);
            siblings = allTags.stream()
                    .filter(t -> t.getParentId() == null)
                    .collect(Collectors.toList());
        }

        if (siblings.isEmpty()) {
            return LexoRankUtils.initialRank(0);
        } else {
            TagEntity lastSibling = siblings.get(siblings.size() - 1);
            return LexoRankUtils.after(lastSibling.getSortRank());
        }
    }

    private TagRsp enrichTagRsp(TagEntity entity) {
        TagRsp rsp = TagMapper.INSTANCE.toRsp(entity);
        tagCategoryRepository.findById(entity.getCategoryId()).ifPresent(category -> {
            rsp.setCategoryName(category.getName());
        });
        return rsp;
    }

    private List<TagTreeRsp> buildTree(List<TagEntity> tags, UUID categoryId) {
        // Group tags by parentId, using ROOT_KEY for null parentId
        Map<UUID, List<TagEntity>> groupedByParent = tags.stream()
                .collect(Collectors.groupingBy(
                        tag -> tag.getParentId() != null ? tag.getParentId() : ROOT_KEY
                ));

        // Get category name for enrichment
        String categoryName = tagCategoryRepository.findById(categoryId)
                .map(TagCategoryEntity::getName)
                .orElse("");

        // Build root nodes (parentId == null)
        List<TagEntity> rootEntities = groupedByParent.getOrDefault(ROOT_KEY, Collections.emptyList());
        return rootEntities.stream()
                .map(entity -> buildTreeNode(entity, groupedByParent, categoryName))
                .collect(Collectors.toList());
    }

    private TagTreeRsp buildTreeNode(TagEntity entity, Map<UUID, List<TagEntity>> groupedByParent, String categoryName) {
        TagTreeRsp node = new TagTreeRsp();
        node.setId(entity.getId());
        node.setName(entity.getName());
        node.setCategoryId(entity.getCategoryId());
        node.setCategoryName(categoryName);
        node.setParentId(entity.getParentId());
        node.setSortRank(entity.getSortRank());

        List<TagEntity> childEntities = groupedByParent.getOrDefault(entity.getId(), Collections.emptyList());
        List<TagTreeRsp> children = childEntities.stream()
                .map(child -> buildTreeNode(child, groupedByParent, categoryName))
                .collect(Collectors.toList());
        node.setChildren(children);

        return node;
    }
}
