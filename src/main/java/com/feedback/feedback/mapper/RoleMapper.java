package com.feedback.feedback.mapper;

import com.feedback.feedback.model.dto.RoleRequestDto;
import com.feedback.feedback.model.dto.RoleResponseDto;
import com.feedback.feedback.model.entity.RoleEntity;
import java.util.List;

public class RoleMapper {

    private RoleMapper(){
        throw new IllegalStateException("Clase de Utilidad(Utility Class)");
    }
    public static RoleEntity toEntity(RoleRequestDto roleRequestDto){
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName(roleRequestDto.getName());
        roleEntity.setDescription(roleRequestDto.getDescription());
        return roleEntity;
    }
    public static RoleResponseDto toDto(RoleEntity roleEntity){
        RoleResponseDto roleResponseDto = new RoleResponseDto();
        roleResponseDto.setId(roleEntity.getId());
        roleResponseDto.setName(roleEntity.getName());
        roleResponseDto.setDescription(roleEntity.getDescription());
        return roleResponseDto;
    }
    public static List<RoleResponseDto> toListDto(List<RoleEntity> roleEntities){
        return roleEntities.stream()
                .map(RoleMapper::toDto)
                .toList();
    }
}
