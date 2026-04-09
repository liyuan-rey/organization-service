package com.reythecoder.organization.service;

import com.reythecoder.organization.dto.response.TreeNodeRsp;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public interface TreeService {

    TreeNodeRsp getTreeByGroupId(@NotNull UUID groupId, Integer depth);
}