package com.feedback.feedback.mapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.feedback.feedback.model.dto.RoleRequestDto;
import com.feedback.feedback.model.dto.RoleResponseDto;
import com.feedback.feedback.model.entity.RoleEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoleMapper {

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
