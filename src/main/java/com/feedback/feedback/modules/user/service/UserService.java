package com.feedback.feedback.modules.user.service;


import com.feedback.feedback.modules.auth.controller.dto.LoginResponseDto;
import com.feedback.feedback.modules.user.model.dto.UserRequestDto;
import com.feedback.feedback.modules.user.model.dto.UserResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {


    UserResponseDto createUser(UserRequestDto userRequestDto);
    UserResponseDto getUserById(Long id);
    LoginResponseDto updateUser(Long id, UserRequestDto userRequestDto);
    List<UserResponseDto> getAllUsers();
    List<UserResponseDto> getAllActiveUsers();
    void deleteUser(Long id);
    UserResponseDto updateUserProfile(Long id, MultipartFile profilePictureUrl);
    UserResponseDto registerAdmin(UserRequestDto userRequestDto);
}
