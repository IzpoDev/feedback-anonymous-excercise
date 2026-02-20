package com.feedback.feedback.mapper;


import com.feedback.feedback.model.dto.UserRequestDto;
import com.feedback.feedback.model.dto.UserResponseDto;
import com.feedback.feedback.model.entity.UserEntity;
import java.util.List;


public class UserMapper {
    private UserMapper(){
        throw new IllegalStateException("Clase de Utilidad(Utility Class)");
    }
    public static UserEntity toEntity( UserRequestDto userRequestDto){
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail( userRequestDto.getEmail() );
        userEntity.setUsername( userRequestDto.getUsername());
        userEntity.setPassword( userRequestDto.getPassword());
        return userEntity;
    }
    public static UserResponseDto toDto(UserEntity userEntity){
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(userEntity.getId());
        userResponseDto.setEmail(userEntity.getEmail());
        userResponseDto.setUsername(userEntity.getUsername());
        userResponseDto.setRole(userEntity.getRole().getName());
        return userResponseDto;
    }
    public static List<UserResponseDto> toListDto(List<UserEntity> userEntities){
        return userEntities.stream()
                .map(UserMapper::toDto)
                .toList();
    }
}
