package com.reythecoder.organization.service;

import com.reythecoder.organization.dto.request.PositionCreateReq;
import com.reythecoder.organization.dto.request.PositionUpdateReq;
import com.reythecoder.organization.dto.response.PositionRsp;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public interface PositionService {
    /**
     * 获取所有岗位
     * 
     * @return 岗位列表
     */
    List<PositionRsp> getAllPositions();

    /**
     * 根据 ID 获取岗位
     * 
     * @param id 岗位 ID
     * @return 岗位信息
     */
    PositionRsp getPositionById(@NotNull UUID id);

    /**
     * 创建岗位
     * 
     * @param req 创建岗位请求
     * @return 创建后的岗位信息
     */
    PositionRsp createPosition(@NotNull PositionCreateReq req);

    /**
     * 更新岗位
     * 
     * @param id  岗位 ID
     * @param req 更新岗位请求
     * @return 更新后的岗位信息
     */
    PositionRsp updatePosition(@NotNull UUID id, @NotNull PositionUpdateReq req);

    /**
     * 删除岗位
     * 
     * @param id 岗位 ID
     */
    void deletePosition(@NotNull UUID id);
}
