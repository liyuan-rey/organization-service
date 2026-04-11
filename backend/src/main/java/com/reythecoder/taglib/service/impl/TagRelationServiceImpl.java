package com.reythecoder.taglib.service.impl;

import com.reythecoder.common.exception.ApiException;
import com.reythecoder.taglib.dto.request.TagRelationQueryReq;
import com.reythecoder.taglib.dto.request.TagRelationReq;
import com.reythecoder.taglib.dto.response.TagRelationRsp;
import com.reythecoder.taglib.entity.TagEntity;
import com.reythecoder.taglib.entity.TagRelationEntity;
import com.reythecoder.taglib.repository.TagRelationRepository;
import com.reythecoder.taglib.repository.TagRepository;
import com.reythecoder.taglib.service.TagRelationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Validated
public class TagRelationServiceImpl implements TagRelationService {

    private static final Logger logger = LoggerFactory.getLogger(TagRelationServiceImpl.class);

    private final TagRelationRepository tagRelationRepository;
    private final TagRepository tagRepository;

    public TagRelationServiceImpl(TagRelationRepository tagRelationRepository, TagRepository tagRepository) {
        this.tagRelationRepository = tagRelationRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public List<TagRelationRsp> getByObject(String objectType, UUID objectId) {
        logger.info("根据对象获取标签关联, objectType: {}, objectId: {}", objectType, objectId);
        List<TagRelationEntity> entities = tagRelationRepository.findByObjectTypeAndObjectId(objectType, objectId);
        return entities.stream()
                .map(this::toRsp)
                .collect(Collectors.toList());
    }

    @Override
    public List<TagRelationRsp> getByTag(UUID tagId) {
        logger.info("根据标签获取关联, tagId: {}", tagId);
        List<TagRelationEntity> entities = tagRelationRepository.findByTagId(tagId);
        return entities.stream()
                .map(this::toRsp)
                .collect(Collectors.toList());
    }

    @Override
    public List<TagRelationRsp> batchCreate(TagRelationReq req) {
        logger.info("批量创建标签关联, objectType: {}, objectId: {}, tagIds: {}", req.getObjectType(), req.getObjectId(), req.getTagIds());
        List<TagRelationRsp> results = new ArrayList<>();

        for (UUID tagId : req.getTagIds()) {
            tagRepository.findByIdAndRemovedFalse(tagId)
                    .orElseThrow(() -> new ApiException(404, "标签不存在: " + tagId));

            if (tagRelationRepository.existsByObjectTypeAndObjectIdAndTagId(req.getObjectType(), req.getObjectId(), tagId)) {
                logger.info("标签关联已存在, 跳过, objectType: {}, objectId: {}, tagId: {}", req.getObjectType(), req.getObjectId(), tagId);
                continue;
            }

            TagRelationEntity entity = new TagRelationEntity(req.getObjectType(), req.getObjectId(), tagId);
            TagRelationEntity saved = tagRelationRepository.save(entity);
            results.add(toRsp(saved));
        }

        return results;
    }

    @Override
    public void delete(UUID id) {
        logger.info("删除标签关联, id: {}", id);
        TagRelationEntity entity = tagRelationRepository.findById(id)
                .orElseThrow(() -> new ApiException(404, "标签关联不存在"));
        tagRelationRepository.delete(entity);
    }

    @Override
    public List<TagRelationRsp> queryByMultipleTags(TagRelationQueryReq req) {
        logger.info("多标签查询关联, objectType: {}, tagIds: {}", req.getObjectType(), req.getTagIds());
        List<TagRelationEntity> entities = tagRelationRepository.findByObjectTypeAndTagIdsWithAllMatch(
                req.getObjectType(), req.getTagIds(), req.getTagIds().size());
        return entities.stream()
                .map(this::toRsp)
                .collect(Collectors.toList());
    }

    private TagRelationRsp toRsp(TagRelationEntity entity) {
        String tagName = tagRepository.findById(entity.getTagId())
                .map(TagEntity::getName)
                .orElse("");

        return new TagRelationRsp(
                entity.getId(),
                entity.getObjectType(),
                entity.getObjectId(),
                "",
                entity.getTagId(),
                tagName,
                entity.getCreateTime(),
                entity.getUpdateTime()
        );
    }
}
