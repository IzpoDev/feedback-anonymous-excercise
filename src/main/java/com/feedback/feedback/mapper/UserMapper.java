package com.feedback.feedback.mapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.feedback.feedback.model.dto.UserRequestDto;
import com.feedback.feedback.model.dto.UserResponseDto;
import com.feedback.feedback.model.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

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
        userResponseDto.setPassword(userEntity.getPassword());
        userResponseDto.setRole(userEntity.getRole().getName());
        return userResponseDto;
    }
    public static List<UserResponseDto> toListDto(List<UserEntity> userEntities){
        return userEntities.stream()
                .map(UserMapper::toDto)
                .toList();
    }
}
