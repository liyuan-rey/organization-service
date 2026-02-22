package com.reythecoder.organization.controller;

import com.reythecoder.organization.dto.request.PersonnelCreateReq;
import com.reythecoder.organization.dto.request.PersonnelUpdateReq;
import com.reythecoder.organization.dto.response.ApiResponse;
import com.reythecoder.organization.dto.response.PersonnelRsp;
import com.reythecoder.organization.service.PersonnelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/personnel")
public class PersonnelController {

    private final PersonnelService personnelService;

    @Autowired
    public PersonnelController(PersonnelService personnelService) {
        this.personnelService = personnelService;
    }

    @GetMapping
    public ApiResponse<List<PersonnelRsp>> getAllPersonnel() {
        List<PersonnelRsp> personnel = personnelService.getAllPersonnel();
        return ApiResponse.success(personnel);
    }

    @GetMapping("/{id}")
    public ApiResponse<PersonnelRsp> getPersonnelById(@PathVariable UUID id) {
        PersonnelRsp personnel = personnelService.getPersonnelById(id);
        return ApiResponse.success(personnel);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PersonnelRsp> createPersonnel(@Valid @RequestBody PersonnelCreateReq req) {
        PersonnelRsp personnel = personnelService.createPersonnel(req);
        return ApiResponse.success("人员创建成功", personnel);
    }

    @PutMapping("/{id}")
    public ApiResponse<PersonnelRsp> updatePersonnel(@PathVariable UUID id, @Valid @RequestBody PersonnelUpdateReq req) {
        PersonnelRsp personnel = personnelService.updatePersonnel(id, req);
        return ApiResponse.success("人员更新成功", personnel);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deletePersonnel(@PathVariable UUID id) {
        personnelService.deletePersonnel(id);
        return ApiResponse.success("人员删除成功", null);
    }
}