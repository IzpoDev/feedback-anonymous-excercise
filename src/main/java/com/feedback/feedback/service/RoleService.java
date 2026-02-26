package com.feedback.feedback.service;

import com.feedback.feedback.model.dto.RoleRequestDto;
import com.feedback.feedback.model.dto.RoleResponseDto;

import java.util.List;

public interface RoleService {

    RoleResponseDto createRole(RoleRequestDto roleRequestDto);
    RoleResponseDto getRoleById(Long id);
    RoleResponseDto updateRole(Long id, RoleRequestDto roleRequestDto);
    List<RoleResponseDto> getAllRoles();
    void deleteRole(Long id);
    // En RoleService.java, agrega estos métodos:
    String assignPrivilegeToRole(Long roleId, Long privilegeId);
    String removePrivilegeFromRole(Long roleId, Long privilegeId);
}
