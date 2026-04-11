package com.reythecoder.taglib.service.impl;

import com.reythecoder.common.utils.LexoRankUtils;
import com.reythecoder.common.exception.ApiException;
import com.reythecoder.taglib.dto.request.TagCategoryCreateReq;
import com.reythecoder.taglib.dto.request.TagCategoryUpdateReq;
import com.reythecoder.taglib.dto.response.TagCategoryRsp;
import com.reythecoder.taglib.entity.TagCategoryEntity;
import com.reythecoder.taglib.mapper.TagCategoryMapper;
import com.reythecoder.taglib.repository.TagCategoryRepository;
import com.reythecoder.taglib.service.TagCategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Validated
public class TagCategoryServiceImpl implements TagCategoryService {

    private static final Logger logger = LoggerFactory.getLogger(TagCategoryServiceImpl.class);

    private final TagCategoryRepository tagCategoryRepository;

    public TagCategoryServiceImpl(TagCategoryRepository tagCategoryRepository) {
        this.tagCategoryRepository = tagCategoryRepository;
    }

    @Override
    public List<TagCategoryRsp> getAllCategories() {
        logger.info("获取所有标签分类列表");
        List<TagCategoryEntity> entities = tagCategoryRepository.findByRemovedFalseOrderBySortRankAsc();
        return entities.stream()
                .map(TagCategoryMapper.INSTANCE::toRsp)
                .collect(Collectors.toList());
    }

    @Override
    public TagCategoryRsp getById(UUID id) {
        logger.info("根据ID获取标签分类, id: {}", id);
        TagCategoryEntity entity = tagCategoryRepository.findById(id)
                .orElseThrow(() -> new ApiException(404, "标签分类不存在"));
        if (entity.isRemoved()) {
            throw new ApiException(404, "标签分类不存在");
        }
        return TagCategoryMapper.INSTANCE.toRsp(entity);
    }

    @Override
    public TagCategoryRsp create(TagCategoryCreateReq req) {
        logger.info("创建标签分类, name: {}", req.getName());

        if (tagCategoryRepository.existsByNameAndRemovedFalse(req.getName())) {
            throw new ApiException(400, "标签分类名称已存在");
        }

        String sortRank;
        if (req.getSortRank() != null && !req.getSortRank().isBlank()) {
            sortRank = req.getSortRank();
        } else {
            List<TagCategoryEntity> existingCategories = tagCategoryRepository.findByRemovedFalseOrderBySortRankAsc();
            if (existingCategories.isEmpty()) {
                sortRank = LexoRankUtils.initialRank(0);
            } else {
                TagCategoryEntity lastCategory = existingCategories.get(existingCategories.size() - 1);
                sortRank = LexoRankUtils.after(lastCategory.getSortRank());
            }
        }

        TagCategoryEntity entity = new TagCategoryEntity(req.getName(), req.getDescription(), sortRank);
        TagCategoryEntity savedEntity = tagCategoryRepository.save(entity);
        return TagCategoryMapper.INSTANCE.toRsp(savedEntity);
    }

    @Override
    public TagCategoryRsp update(UUID id, TagCategoryUpdateReq req) {
        logger.info("更新标签分类, id: {}", id);
        TagCategoryEntity entity = tagCategoryRepository.findById(id)
                .orElseThrow(() -> new ApiException(404, "标签分类不存在"));
        if (entity.isRemoved()) {
            throw new ApiException(404, "标签分类不存在");
        }

        TagCategoryMapper.INSTANCE.updateEntity(req, entity);
        TagCategoryEntity savedEntity = tagCategoryRepository.save(entity);
        return TagCategoryMapper.INSTANCE.toRsp(savedEntity);
    }

    @Override
    public void delete(UUID id) {
        logger.info("删除标签分类, id: {}", id);
        TagCategoryEntity entity = tagCategoryRepository.findById(id)
                .orElseThrow(() -> new ApiException(404, "标签分类不存在"));
        entity.setRemoved(true);
        tagCategoryRepository.save(entity);
    }
}
