package com.feedback.feedback.config;

import com.feedback.feedback.common.util.UserCacheService;
import com.feedback.feedback.modules.privilege.entity.PrivilegeEntity;
import com.feedback.feedback.modules.privilege.repository.RolePrivilegeRepository;
import com.feedback.feedback.modules.user.model.dto.UserCacheDto;
import lombok.RequiredArgsConstructor;
import com.feedback.feedback.modules.user.model.entity.UserEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.feedback.feedback.modules.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CustomerUserDetailService implements UserDetailsService {
    private final UserCacheService userCacheService;

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        // 1. Al llamar a OTRO servicio, cruzamos la frontera del Proxy y el caché se activa
        UserCacheDto cachedData = userCacheService.getUserDataForCache(username);

        // 2. Reconstruimos los permisos al vuelo
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String auth : cachedData.getAuthorities()) {
            authorities.add(new SimpleGrantedAuthority(auth));
        }

        return new User(cachedData.getUsername(), cachedData.getPassword(), authorities);
    }
}