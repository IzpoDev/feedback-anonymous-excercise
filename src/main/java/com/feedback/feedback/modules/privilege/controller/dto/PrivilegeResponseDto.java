package com.feedback.feedback.modules.privilege.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PrivilegeResponseDto {
    private Long id;
    private String name;
    private String description;
}
