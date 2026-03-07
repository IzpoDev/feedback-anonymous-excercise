package com.feedback.feedback.modules.privilege.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RolePrivilegeResponseDto {
    private Long rolePrivilegeId;
    private String roleName;
    private String privilegeName;
}
