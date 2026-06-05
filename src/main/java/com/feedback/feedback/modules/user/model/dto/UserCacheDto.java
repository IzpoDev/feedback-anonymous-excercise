package com.feedback.feedback.modules.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor //Vital para que jackson pueda crear el json
@AllArgsConstructor
public class UserCacheDto {

    private String username;
    private String password;
    private List<String> authorities;
}
