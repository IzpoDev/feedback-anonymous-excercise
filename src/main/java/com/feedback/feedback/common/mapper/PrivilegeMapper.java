package com.feedback.feedback.common.mapper;

import com.feedback.feedback.modules.privilege.controller.dto.PrivilegeRequestDto;
import com.feedback.feedback.modules.privilege.controller.dto.PrivilegeResponseDto;
import com.feedback.feedback.modules.privilege.entity.PrivilegeEntity;

import java.util.List;


public class PrivilegeMapper {

    private PrivilegeMapper(){
        throw new IllegalStateException("Clase de Utilidad(Utility Class)");
    }
    public static PrivilegeEntity toEntity(PrivilegeRequestDto privilegeRequestDto){
        PrivilegeEntity privilegeEntity = new PrivilegeEntity();
        privilegeEntity.setName(privilegeRequestDto.getName());
        privilegeEntity.setDescription(privilegeRequestDto.getDescription());
        return privilegeEntity;
    }
    public static PrivilegeResponseDto toDto(PrivilegeEntity privilegeEntity){
        PrivilegeResponseDto privilegeResponseDto = new PrivilegeResponseDto();
        privilegeResponseDto.setName(privilegeEntity.getName());
        privilegeResponseDto.setDescription(privilegeEntity.getDescription());
        privilegeResponseDto.setId(privilegeEntity.getId());
        return privilegeResponseDto;
    }
    public static List<PrivilegeResponseDto> toListDto(List<PrivilegeEntity> privilegeEntities){
        return privilegeEntities.stream()
                .map(PrivilegeMapper::toDto)
                .toList();

    }
}