package com.reythecoder.organization.controller;

import com.reythecoder.organization.dto.response.ApiResult;
import com.reythecoder.organization.dto.response.TreeNodeRsp;
import com.reythecoder.organization.service.TreeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/trees")
public class TreeController {

    private static final Logger logger = LoggerFactory.getLogger(TreeController.class);

    private final TreeService treeService;

    public TreeController(TreeService treeService) {
        this.treeService = treeService;
    }

    @GetMapping("/{groupId}")
    public ApiResult<TreeNodeRsp> getTree(@PathVariable UUID groupId,
                                          @RequestParam(required = false) Integer depth) {
        logger.info("收到获取树结构请求, groupId: {}, depth: {}", groupId, depth);
        TreeNodeRsp tree = treeService.getTreeByGroupId(groupId, depth);
        return ApiResult.success(tree);
    }
}