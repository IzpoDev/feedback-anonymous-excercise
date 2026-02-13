package com.feedback.feedback.service;

import com.feedback.feedback.model.dto.PrivilegeRequestDto;
import com.feedback.feedback.model.dto.PrivilegeResponseDto;

import java.util.List;

public interface PrivilegeService {
    PrivilegeResponseDto createPrivilege(PrivilegeRequestDto privilegeRequestDto);
    PrivilegeResponseDto getPrivilegeById(Long id);
    PrivilegeResponseDto updatePrivilege(Long id, PrivilegeRequestDto privilegeRequestDto);
    List<PrivilegeResponseDto> getAllPrivileges();
    void deletePrivilege(Long id);
}
