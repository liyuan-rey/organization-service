package com.reythecoder.organization.service;

import com.reythecoder.organization.dto.request.PersonnelCreateReq;
import com.reythecoder.organization.dto.request.PersonnelUpdateReq;
import com.reythecoder.organization.dto.response.PersonnelRsp;

import java.util.List;
import java.util.UUID;

public interface PersonnelService {
    List<PersonnelRsp> getAllPersonnel();
    PersonnelRsp getPersonnelById(UUID id);
    PersonnelRsp createPersonnel(PersonnelCreateReq req);
    PersonnelRsp updatePersonnel(UUID id, PersonnelUpdateReq req);
    void deletePersonnel(UUID id);
}