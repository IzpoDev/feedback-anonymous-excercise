package com.feedback.feedback.modules.privilege.service;

import com.feedback.feedback.modules.privilege.controller.dto.PrivilegeRequestDto;
import com.feedback.feedback.modules.privilege.controller.dto.PrivilegeResponseDto;
import com.feedback.feedback.modules.privilege.controller.dto.RolePrivilegeResponseDto;

import java.util.List;

public interface PrivilegeService {
    PrivilegeResponseDto createPrivilege(PrivilegeRequestDto privilegeRequestDto);
    PrivilegeResponseDto getPrivilegeById(Long id);
    PrivilegeResponseDto updatePrivilege(Long id, PrivilegeRequestDto privilegeRequestDto);
    List<PrivilegeResponseDto> getAllPrivileges();
    List<RolePrivilegeResponseDto> getRolesWithPrivilege();
    void deletePrivilege(Long id);
}
