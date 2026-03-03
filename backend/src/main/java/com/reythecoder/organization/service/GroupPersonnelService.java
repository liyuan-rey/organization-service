package com.reythecoder.organization.service;

import com.reythecoder.organization.dto.request.GroupPersonnelCreateReq;
import com.reythecoder.organization.dto.response.GroupPersonnelRsp;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public interface GroupPersonnelService {
    
    List<GroupPersonnelRsp> getByGroupId(@NotNull UUID groupId);
    
    List<GroupPersonnelRsp> getByPersonnelId(@NotNull UUID personnelId);
    
    GroupPersonnelRsp create(@NotNull GroupPersonnelCreateReq req);
    
    void delete(@NotNull UUID groupId, @NotNull UUID personnelId);
}