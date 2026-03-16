package com.feedback.feedback.modules.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserResponseDto {
    private Long id;
    private String profilePictureUrl;
    private String username;
    private String email;
    private String role;
}
