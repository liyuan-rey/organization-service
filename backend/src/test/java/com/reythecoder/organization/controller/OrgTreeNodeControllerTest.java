package com.reythecoder.organization.controller;

import com.reythecoder.organization.dto.request.CreateTreeNodeReq;
import com.reythecoder.organization.dto.request.MoveTreeNodeReq;
import com.reythecoder.organization.dto.request.UpdateTreeNodeReq;
import com.reythecoder.organization.dto.response.TreeNodeRsp;
import com.reythecoder.organization.dto.response.TreeStatistics;
import com.reythecoder.organization.entity.EntityType;
import com.reythecoder.organization.service.OrgTreeNodeService;
import io.github.robsonkades.uuidv7.UUIDv7;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for OrgTreeNodeController.
 */
@Import(com.reythecoder.common.exception.GlobalExceptionHandler.class)
@WebMvcTest(OrgTreeNodeController.class)
class OrgTreeNodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrgTreeNodeService orgTreeNodeService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID nodeId;
    private UUID parentId;
    private UUID entityId;
    private TreeNodeRsp treeNodeRsp;
    private CreateTreeNodeReq createReq;
    private UpdateTreeNodeReq updateReq;
    private MoveTreeNodeReq moveReq;

    @BeforeEach
    void setUp() {
        nodeId = UUIDv7.randomUUID();
        parentId = UUIDv7.randomUUID();
        entityId = UUIDv7.randomUUID();

        treeNodeRsp = new TreeNodeRsp();
        treeNodeRsp.setId(nodeId);
        treeNodeRsp.setType(EntityType.DEPARTMENT);
        treeNodeRsp.setName("测试节点");
        treeNodeRsp.setSortOrder(1);
        treeNodeRsp.setStatistics(new TreeStatistics(0, 0, 0));
        treeNodeRsp.setChildren(Collections.emptyList());

        createReq = new CreateTreeNodeReq(
                parentId,
                EntityType.DEPARTMENT,
                entityId,
                "测试节点",
                "00001");

        updateReq = new UpdateTreeNodeReq(
                nodeId,
                "更新后的节点",
                "00002");

        moveReq = new MoveTreeNodeReq(
                nodeId,
                parentId,
                "00003");
    }

    @Test
    void createNode_shouldReturnCreatedNode() throws Exception {
        // Arrange
        when(orgTreeNodeService.createNode(parentId, EntityType.DEPARTMENT, entityId, "测试节点"))
                .thenReturn(treeNodeRsp);

        // Act & Assert
        mockMvc.perform(post("/api/tree/nodes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("节点创建成功"))
                .andExpect(jsonPath("$.data.id").value(nodeId.toString()))
                .andExpect(jsonPath("$.data.type").value("DEPARTMENT"))
                .andExpect(jsonPath("$.data.name").value("测试节点"));

        verify(orgTreeNodeService, times(1)).createNode(parentId, EntityType.DEPARTMENT, entityId, "测试节点");
    }

    @Test
    void getNode_shouldReturnNode() throws Exception {
        // Arrange
        when(orgTreeNodeService.getNode(nodeId)).thenReturn(treeNodeRsp);

        // Act & Assert
        mockMvc.perform(get("/api/tree/nodes/{nodeId}", nodeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.id").value(nodeId.toString()))
                .andExpect(jsonPath("$.data.name").value("测试节点"));

        verify(orgTreeNodeService, times(1)).getNode(nodeId);
    }

    @Test
    void updateNode_shouldReturnUpdatedNode() throws Exception {
        // Arrange
        TreeNodeRsp updatedRsp = new TreeNodeRsp();
        updatedRsp.setId(nodeId);
        updatedRsp.setType(EntityType.DEPARTMENT);
        updatedRsp.setName("更新后的节点");
        updatedRsp.setSortOrder(1);
        updatedRsp.setStatistics(new TreeStatistics(0, 0, 0));
        updatedRsp.setChildren(Collections.emptyList());

        when(orgTreeNodeService.updateNode(nodeId, "更新后的节点", "00002")).thenReturn(updatedRsp);

        // Act & Assert
        mockMvc.perform(put("/api/tree/nodes/{nodeId}", nodeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("节点更新成功"))
                .andExpect(jsonPath("$.data.id").value(nodeId.toString()))
                .andExpect(jsonPath("$.data.name").value("更新后的节点"));

        verify(orgTreeNodeService, times(1)).updateNode(nodeId, "更新后的节点", "00002");
    }

    @Test
    void removeNode_shouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(orgTreeNodeService).removeNode(nodeId);

        // Act & Assert
        mockMvc.perform(post("/api/tree/nodes/{nodeId}/remove", nodeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(orgTreeNodeService, times(1)).removeNode(nodeId);
    }

    @Test
    void moveNode_shouldReturnMovedNode() throws Exception {
        // Arrange
        when(orgTreeNodeService.moveNode(nodeId, parentId)).thenReturn(treeNodeRsp);

        // Act & Assert
        mockMvc.perform(post("/api/tree/nodes/{nodeId}/move", nodeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(moveReq)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("节点移动成功"))
                .andExpect(jsonPath("$.data.id").value(nodeId.toString()));

        verify(orgTreeNodeService, times(1)).moveNode(nodeId, parentId);
    }

    @Test
    void getChildren_shouldReturnListOfChildren() throws Exception {
        // Arrange
        TreeNodeRsp child1 = new TreeNodeRsp();
        child1.setId(UUIDv7.randomUUID());
        child1.setType(EntityType.DEPARTMENT);
        child1.setName("子节点 1");
        child1.setSortOrder(1);
        child1.setStatistics(new TreeStatistics(0, 0, 0));
        child1.setChildren(Collections.emptyList());

        TreeNodeRsp child2 = new TreeNodeRsp();
        child2.setId(UUIDv7.randomUUID());
        child2.setType(EntityType.GROUP);
        child2.setName("子节点 2");
        child2.setSortOrder(2);
        child2.setStatistics(new TreeStatistics(0, 0, 0));
        child2.setChildren(Collections.emptyList());

        List<TreeNodeRsp> children = Arrays.asList(child1, child2);
        when(orgTreeNodeService.getChildren(parentId)).thenReturn(children);

        // Act & Assert
        mockMvc.perform(get("/api/tree/nodes/{nodeId}/children", parentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("子节点 1"))
                .andExpect(jsonPath("$.data[1].name").value("子节点 2"));

        verify(orgTreeNodeService, times(1)).getChildren(parentId);
    }

    @Test
    void getChildren_shouldReturnEmptyListWhenNoChildren() throws Exception {
        // Arrange
        when(orgTreeNodeService.getChildren(parentId)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/tree/nodes/{nodeId}/children", parentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());

        verify(orgTreeNodeService, times(1)).getChildren(parentId);
    }

    @Test
    void getSubtree_shouldReturnSubtreeWithDefaultDepth() throws Exception {
        // Arrange
        TreeNodeRsp subtree = new TreeNodeRsp();
        subtree.setId(nodeId);
        subtree.setType(EntityType.DEPARTMENT);
        subtree.setName("根节点");
        subtree.setSortOrder(0);
        subtree.setStatistics(new TreeStatistics(2, 5, 10));
        subtree.setChildren(Collections.emptyList());

        when(orgTreeNodeService.getSubTree(nodeId, null)).thenReturn(subtree);

        // Act & Assert
        mockMvc.perform(get("/api/tree/nodes/{nodeId}/subtree", nodeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.id").value(nodeId.toString()))
                .andExpect(jsonPath("$.data.name").value("根节点"))
                .andExpect(jsonPath("$.data.statistics.subGroupCount").value(2))
                .andExpect(jsonPath("$.data.statistics.subDepartmentCount").value(5))
                .andExpect(jsonPath("$.data.statistics.personnelCount").value(10));

        verify(orgTreeNodeService, times(1)).getSubTree(nodeId, null);
    }

    @Test
    void getSubtree_shouldReturnSubtreeWithSpecifiedDepth() throws Exception {
        // Arrange
        when(orgTreeNodeService.getSubTree(nodeId, 2)).thenReturn(treeNodeRsp);

        // Act & Assert
        mockMvc.perform(get("/api/tree/nodes/{nodeId}/subtree", nodeId)
                        .param("depth", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("测试节点"));

        verify(orgTreeNodeService, times(1)).getSubTree(nodeId, 2);
    }

    @Test
    void getDescendants_shouldReturnListOfDescendants() throws Exception {
        // Arrange
        TreeNodeRsp descendant1 = new TreeNodeRsp();
        descendant1.setId(UUIDv7.randomUUID());
        descendant1.setType(EntityType.DEPARTMENT);
        descendant1.setName("后代节点 1");
        descendant1.setSortOrder(1);
        descendant1.setStatistics(new TreeStatistics(0, 0, 0));
        descendant1.setChildren(Collections.emptyList());

        List<TreeNodeRsp> descendants = Collections.singletonList(descendant1);
        when(orgTreeNodeService.getAllDescendants(nodeId)).thenReturn(descendants);

        // Act & Assert
        mockMvc.perform(get("/api/tree/nodes/{nodeId}/descendants", nodeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].name").value("后代节点 1"));

        verify(orgTreeNodeService, times(1)).getAllDescendants(nodeId);
    }

    @Test
    void getAncestors_shouldReturnListOfAncestors() throws Exception {
        // Arrange
        TreeNodeRsp ancestor1 = new TreeNodeRsp();
        ancestor1.setId(UUIDv7.randomUUID());
        ancestor1.setType(EntityType.GROUP);
        ancestor1.setName("祖先节点 1");
        ancestor1.setSortOrder(0);
        ancestor1.setStatistics(new TreeStatistics(0, 0, 0));
        ancestor1.setChildren(Collections.emptyList());

        List<TreeNodeRsp> ancestors = Collections.singletonList(ancestor1);
        when(orgTreeNodeService.getAllAncestors(nodeId)).thenReturn(ancestors);

        // Act & Assert
        mockMvc.perform(get("/api/tree/nodes/{nodeId}/ancestors", nodeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].name").value("祖先节点 1"));

        verify(orgTreeNodeService, times(1)).getAllAncestors(nodeId);
    }

    @Test
    void getRootNode_shouldReturnRootNode() throws Exception {
        // Arrange
        TreeNodeRsp root = new TreeNodeRsp();
        root.setId(UUIDv7.randomUUID());
        root.setType(EntityType.ROOT);
        root.setName("根节点");
        root.setSortOrder(0);
        root.setStatistics(new TreeStatistics(2, 5, 10));
        root.setChildren(Collections.emptyList());

        when(orgTreeNodeService.getRootNode()).thenReturn(root);

        // Act & Assert
        mockMvc.perform(get("/api/tree/nodes/root")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.id").value(root.getId().toString()))
                .andExpect(jsonPath("$.data.type").value("ROOT"))
                .andExpect(jsonPath("$.data.name").value("根节点"))
                .andExpect(jsonPath("$.data.statistics.subGroupCount").value(2))
                .andExpect(jsonPath("$.data.statistics.subDepartmentCount").value(5))
                .andExpect(jsonPath("$.data.statistics.personnelCount").value(10));

        verify(orgTreeNodeService, times(1)).getRootNode();
    }

    @Test
    void getRootChildren_shouldReturnListOfRootChildren() throws Exception {
        // Arrange
        UUID rootId = UUIDv7.randomUUID();
        TreeNodeRsp root = new TreeNodeRsp();
        root.setId(rootId);
        root.setType(EntityType.ROOT);
        root.setName("根节点");
        root.setSortOrder(0);
        root.setStatistics(new TreeStatistics(0, 0, 0));
        root.setChildren(Collections.emptyList());

        TreeNodeRsp child1 = new TreeNodeRsp();
        child1.setId(UUIDv7.randomUUID());
        child1.setType(EntityType.GROUP);
        child1.setName("子节点 1");
        child1.setSortOrder(1);
        child1.setStatistics(new TreeStatistics(0, 0, 0));
        child1.setChildren(Collections.emptyList());

        List<TreeNodeRsp> children = Collections.singletonList(child1);
        when(orgTreeNodeService.getRootNode()).thenReturn(root);
        when(orgTreeNodeService.getChildren(rootId)).thenReturn(children);

        // Act & Assert
        mockMvc.perform(get("/api/tree/nodes/root/children")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].name").value("子节点 1"))
                .andExpect(jsonPath("$.data[0].type").value("GROUP"));

        verify(orgTreeNodeService, times(1)).getRootNode();
        verify(orgTreeNodeService, times(1)).getChildren(rootId);
    }
}
