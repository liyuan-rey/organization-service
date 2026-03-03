package com.reythecoder.organization.controller;

import tools.jackson.databind.ObjectMapper;
import com.reythecoder.organization.dto.request.GroupCreateReq;
import com.reythecoder.organization.dto.request.GroupUpdateReq;
import com.reythecoder.organization.dto.response.GroupRsp;
import com.reythecoder.organization.service.GroupService;

import io.github.robsonkades.uuidv7.UUIDv7;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GroupController.class)
class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GroupService groupService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID groupId;
    private GroupRsp groupRsp;
    private GroupCreateReq groupCreateReq;
    private GroupUpdateReq groupUpdateReq;

    @BeforeEach
    void setUp() {
        groupId = UUIDv7.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        groupRsp = new GroupRsp(
                groupId,
                "测试分组",
                "测试分组描述",
                now,
                now);

        groupCreateReq = new GroupCreateReq(
                "测试分组",
                "测试分组描述");

        groupUpdateReq = new GroupUpdateReq(
                "更新后的分组",
                "更新后的分组描述");
    }

    @Test
    void getAllGroups_shouldReturnAllGroups() throws Exception {
        List<GroupRsp> groups = Collections.singletonList(groupRsp);
        when(groupService.getAllGroups()).thenReturn(groups);

        mockMvc.perform(get("/api/groups"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(groupId.toString()))
                .andExpect(jsonPath("$.data[0].name").value("测试分组"));

        verify(groupService, times(1)).getAllGroups();
    }

    @Test
    void getAllGroups_shouldReturnEmptyListWhenNoGroups() throws Exception {
        when(groupService.getAllGroups()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/groups"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());

        verify(groupService, times(1)).getAllGroups();
    }

    @Test
    void getGroupById_shouldReturnGroupWhenExists() throws Exception {
        when(groupService.getGroupById(groupId)).thenReturn(groupRsp);

        mockMvc.perform(get("/api/groups/{id}", groupId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.id").value(groupId.toString()))
                .andExpect(jsonPath("$.data.name").value("测试分组"));

        verify(groupService, times(1)).getGroupById(groupId);
    }

    @Test
    void createGroup_shouldReturnCreatedGroup() throws Exception {
        when(groupService.createGroup(groupCreateReq)).thenReturn(groupRsp);

        mockMvc.perform(post("/api/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(groupCreateReq)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("分组创建成功"))
                .andExpect(jsonPath("$.data.id").value(groupId.toString()))
                .andExpect(jsonPath("$.data.name").value("测试分组"));

        verify(groupService, times(1)).createGroup(groupCreateReq);
    }

    @Test
    void updateGroup_shouldReturnUpdatedGroup() throws Exception {
        GroupRsp updatedRsp = new GroupRsp(
                groupId,
                "更新后的分组",
                "更新后的分组描述",
                groupRsp.getCreateTime(),
                OffsetDateTime.now());

        when(groupService.updateGroup(groupId, groupUpdateReq)).thenReturn(updatedRsp);

        mockMvc.perform(put("/api/groups/{id}", groupId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(groupUpdateReq)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("分组更新成功"))
                .andExpect(jsonPath("$.data.id").value(groupId.toString()))
                .andExpect(jsonPath("$.data.name").value("更新后的分组"));

        verify(groupService, times(1)).updateGroup(groupId, groupUpdateReq);
    }

    @Test
    void deleteGroup_shouldReturnNoContent() throws Exception {
        doNothing().when(groupService).deleteGroup(groupId);

        mockMvc.perform(delete("/api/groups/{id}", groupId))
                .andExpect(status().isNoContent());

        verify(groupService, times(1)).deleteGroup(groupId);
    }
}