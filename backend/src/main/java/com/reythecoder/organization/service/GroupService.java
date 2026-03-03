package com.reythecoder.organization.service;

import com.reythecoder.organization.dto.request.GroupCreateReq;
import com.reythecoder.organization.dto.request.GroupUpdateReq;
import com.reythecoder.organization.dto.response.GroupRsp;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public interface GroupService {
    
    List<GroupRsp> getAllGroups();
    
    GroupRsp getGroupById(@NotNull UUID id);
    
    GroupRsp createGroup(@NotNull GroupCreateReq req);
    
    GroupRsp updateGroup(@NotNull UUID id, @NotNull GroupUpdateReq req);
    
    void deleteGroup(@NotNull UUID id);
}