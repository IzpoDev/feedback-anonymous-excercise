package com.feedback.feedback.service;


import com.feedback.feedback.model.dto.UserRequestDto;
import com.feedback.feedback.model.dto.UserResponseDto;

import java.util.List;

public interface UserService {


    UserResponseDto createUser(UserRequestDto userRequestDto);
    UserResponseDto getUserById(Long id);
    UserResponseDto updateUser(Long id, UserRequestDto userRequestDto);
    List<UserResponseDto> getAllUsers();
    List<UserResponseDto> getAllActiveUsers();
    void deleteUser(Long id);

    UserResponseDto registerAdmin(UserRequestDto userRequestDto);
}
