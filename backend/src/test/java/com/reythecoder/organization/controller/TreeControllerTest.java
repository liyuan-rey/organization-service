package com.reythecoder.organization.controller;

import com.reythecoder.organization.dto.NodeType;
import com.reythecoder.organization.dto.response.TreeNodeRsp;
import com.reythecoder.organization.dto.response.TreeStatistics;
import com.reythecoder.organization.service.TreeService;
import io.github.robsonkades.uuidv7.UUIDv7;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TreeControllerTest {

    @Mock
    private TreeService treeService;

    @InjectMocks
    private TreeController treeController;

    private MockMvc mockMvc;
    private UUID groupId;
    private TreeNodeRsp treeRsp;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(treeController).build();
        groupId = UUIDv7.randomUUID();

        treeRsp = new TreeNodeRsp();
        treeRsp.setId(groupId);
        treeRsp.setType(NodeType.GROUP);
        treeRsp.setName("基础通讯录");
        treeRsp.setSortOrder(1);
        treeRsp.setStatistics(new TreeStatistics(2, 5, 10));
    }

    @Test
    void getTree_shouldReturnTreeWithDefaultDepth() throws Exception {
        when(treeService.getTreeByGroupId(groupId, null)).thenReturn(treeRsp);

        mockMvc.perform(get("/api/trees/{groupId}", groupId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(groupId.toString()))
                .andExpect(jsonPath("$.data.type").value("GROUP"))
                .andExpect(jsonPath("$.data.name").value("基础通讯录"))
                .andExpect(jsonPath("$.data.statistics.subGroupCount").value(2))
                .andExpect(jsonPath("$.data.statistics.subDepartmentCount").value(5))
                .andExpect(jsonPath("$.data.statistics.personnelCount").value(10));

        verify(treeService, times(1)).getTreeByGroupId(groupId, null);
    }

    @Test
    void getTree_shouldReturnTreeWithSpecifiedDepth() throws Exception {
        when(treeService.getTreeByGroupId(groupId, 2)).thenReturn(treeRsp);

        mockMvc.perform(get("/api/trees/{groupId}", groupId)
                        .param("depth", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("基础通讯录"));

        verify(treeService, times(1)).getTreeByGroupId(groupId, 2);
    }
}