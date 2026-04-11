package com.reythecoder.organization.controller;

import com.reythecoder.organization.dto.request.CreateTreeNodeReq;
import com.reythecoder.organization.dto.request.MoveTreeNodeReq;
import com.reythecoder.organization.dto.request.UpdateTreeNodeReq;
import com.reythecoder.common.dto.ApiResult;
import com.reythecoder.organization.dto.response.TreeNodeRsp;
import com.reythecoder.organization.service.OrgTreeNodeService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller for organization tree node operations.
 */
@RestController
@RequestMapping("/api/tree/nodes")
public class OrgTreeNodeController {

    private static final Logger logger = LoggerFactory.getLogger(OrgTreeNodeController.class);

    private final OrgTreeNodeService orgTreeNodeService;

    public OrgTreeNodeController(OrgTreeNodeService orgTreeNodeService) {
        this.orgTreeNodeService = orgTreeNodeService;
    }

    /**
     * Create a new tree node.
     *
     * @param req the create request
     * @return the created node
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResult<TreeNodeRsp> createNode(@Valid @RequestBody CreateTreeNodeReq req) {
        logger.info("收到创建树节点请求：parentId={}, entityType={}, entityId={}, alias={}",
                req.getParentId(), req.getEntityType(), req.getEntityId(), req.getAlias());

        TreeNodeRsp node = orgTreeNodeService.createNode(
                req.getParentId(),
                req.getEntityType(),
                req.getEntityId(),
                req.getAlias());

        return ApiResult.success("节点创建成功", node);
    }

    /**
     * Get a tree node by ID.
     *
     * @param nodeId the node ID
     * @return the node
     */
    @GetMapping("/{nodeId}")
    public ApiResult<TreeNodeRsp> getNode(@PathVariable UUID nodeId) {
        logger.info("收到获取树节点请求：nodeId={}", nodeId);

        TreeNodeRsp node = orgTreeNodeService.getNode(nodeId);
        return ApiResult.success(node);
    }

    /**
     * Update a tree node.
     *
     * @param nodeId the node ID
     * @param req the update request
     * @return the updated node
     */
    @PutMapping("/{nodeId}")
    public ApiResult<TreeNodeRsp> updateNode(@PathVariable UUID nodeId,
                                             @Valid @RequestBody UpdateTreeNodeReq req) {
        logger.info("收到更新树节点请求：nodeId={}, alias={}, sortRank={}",
                nodeId, req.getAlias(), req.getSortRank());

        TreeNodeRsp node = orgTreeNodeService.updateNode(nodeId, req.getAlias(), req.getSortRank());
        return ApiResult.success("节点更新成功", node);
    }

    /**
     * Remove a tree node.
     *
     * @param nodeId the node ID
     * @return success response
     */
    @PostMapping("/{nodeId}/remove")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResult<Void> removeNode(@PathVariable UUID nodeId) {
        logger.info("收到删除树节点请求：nodeId={}", nodeId);

        orgTreeNodeService.removeNode(nodeId);
        return ApiResult.success("节点删除成功", null);
    }

    /**
     * Move a tree node to a new parent.
     *
     * @param nodeId the node ID
     * @param req the move request
     * @return the moved node
     */
    @PostMapping("/{nodeId}/move")
    public ApiResult<TreeNodeRsp> moveNode(@PathVariable UUID nodeId,
                                           @Valid @RequestBody MoveTreeNodeReq req) {
        logger.info("收到移动树节点请求：nodeId={}, newParentId={}", nodeId, req.getNewParentId());

        TreeNodeRsp node = orgTreeNodeService.moveNode(nodeId, req.getNewParentId());
        return ApiResult.success("节点移动成功", node);
    }

    /**
     * Get all direct children of a parent node.
     *
     * @param nodeId the parent node ID
     * @return list of child nodes
     */
    @GetMapping("/{nodeId}/children")
    public ApiResult<List<TreeNodeRsp>> getChildren(@PathVariable UUID nodeId) {
        logger.info("收到获取子节点列表请求：nodeId={}", nodeId);

        List<TreeNodeRsp> children = orgTreeNodeService.getChildren(nodeId);
        return ApiResult.success(children);
    }

    /**
     * Get a subtree starting from the specified node.
     *
     * @param nodeId the root node ID of the subtree
     * @param depth the maximum depth to load (-1 for unlimited)
     * @return the subtree
     */
    @GetMapping("/{nodeId}/subtree")
    public ApiResult<TreeNodeRsp> getSubtree(@PathVariable UUID nodeId,
                                             @RequestParam(required = false) Integer depth) {
        logger.info("收到获取子树请求：nodeId={}, depth={}", nodeId, depth);

        TreeNodeRsp subtree = orgTreeNodeService.getSubTree(nodeId, depth);
        return ApiResult.success(subtree);
    }

    /**
     * Get all descendant nodes of a given node.
     *
     * @param nodeId the node ID
     * @return list of all descendant nodes
     */
    @GetMapping("/{nodeId}/descendants")
    public ApiResult<List<TreeNodeRsp>> getDescendants(@PathVariable UUID nodeId) {
        logger.info("收到获取所有后代节点请求：nodeId={}", nodeId);

        List<TreeNodeRsp> descendants = orgTreeNodeService.getAllDescendants(nodeId);
        return ApiResult.success(descendants);
    }

    /**
     * Get all ancestor nodes of a given node.
     *
     * @param nodeId the node ID
     * @return list of all ancestor nodes (from root to parent)
     */
    @GetMapping("/{nodeId}/ancestors")
    public ApiResult<List<TreeNodeRsp>> getAncestors(@PathVariable UUID nodeId) {
        logger.info("收到获取所有祖先节点请求：nodeId={}", nodeId);

        List<TreeNodeRsp> ancestors = orgTreeNodeService.getAllAncestors(nodeId);
        return ApiResult.success(ancestors);
    }

    /**
     * Get the root node of the organization tree.
     *
     * @return the root node
     */
    @GetMapping("/root")
    public ApiResult<TreeNodeRsp> getRootNode() {
        logger.info("收到获取根节点请求");

        TreeNodeRsp root = orgTreeNodeService.getRootNode();
        return ApiResult.success(root);
    }

    /**
     * Get all direct children of the root node.
     *
     * @return list of root's child nodes
     */
    @GetMapping("/root/children")
    public ApiResult<List<TreeNodeRsp>> getRootChildren() {
        logger.info("收到获取根节点子节点请求");

        // Get the root node first
        TreeNodeRsp root = orgTreeNodeService.getRootNode();
        List<TreeNodeRsp> children = orgTreeNodeService.getChildren(root.getId());
        return ApiResult.success(children);
    }
}
