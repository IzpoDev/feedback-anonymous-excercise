package com.feedback.feedback.common.mapper;

import com.feedback.feedback.modules.privilege.controller.dto.RolePrivilegeResponseDto;
import com.feedback.feedback.modules.privilege.entity.RolePrivilegeEntity;

import java.util.List;

public class RolePrivilegeMapper {
    public static RolePrivilegeResponseDto toDto(RolePrivilegeEntity rolePrivilegeEntity) {
        RolePrivilegeResponseDto rolePrivilegeResponseDto = new RolePrivilegeResponseDto();
        rolePrivilegeResponseDto.setRolePrivilegeId(rolePrivilegeEntity.getId());
        rolePrivilegeResponseDto.setRoleName(rolePrivilegeEntity.getRole().getName());
        rolePrivilegeResponseDto.setPrivilegeName(rolePrivilegeEntity.getPrivilege().getName());
        return rolePrivilegeResponseDto;
    }
    public static List<RolePrivilegeResponseDto> toListDto(List<RolePrivilegeEntity> rolePrivilegeEntityList) {
        return rolePrivilegeEntityList.stream()
                .map(RolePrivilegeMapper::toDto)
                .toList();
    }
}
