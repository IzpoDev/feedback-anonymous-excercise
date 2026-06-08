package com.feedback.feedback.common.util;

import com.feedback.feedback.common.exception.EntityNotFoundException;
import com.feedback.feedback.modules.privilege.entity.PrivilegeEntity;
import com.feedback.feedback.modules.privilege.repository.RolePrivilegeRepository;
import com.feedback.feedback.modules.user.model.dto.UserCacheDto;
import com.feedback.feedback.modules.user.model.entity.UserEntity;
import com.feedback.feedback.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserCacheService {

    private final UserRepository userRepository;
    private final RolePrivilegeRepository rolePrivilegeRepository;

    @Cacheable(value = "userDetails" , key = "#username")
    public UserCacheDto getUserDataForCache(String username) {
        UserEntity user = userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("Usuario no encontrado")
        );
        List<String> authorityLists = new ArrayList<>();
        authorityLists.add("ROLE_" + user.getRole().getName());
List<PrivilegeEntity> privileges = rolePrivilegeRepository.findPrivilegesByRoleId(user.getRole().getId());
        for (PrivilegeEntity privilege : privileges) {
            authorityLists.add(privilege.getName());
        }
        return new UserCacheDto(user.getUsername(), user.getPassword(), authorityLists);
    }

}
