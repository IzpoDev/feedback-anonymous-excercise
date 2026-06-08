package com.feedback.feedback.config;

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
    private final UserRepository userRepository;
    private final RolePrivilegeRepository rolePrivilegeRepository;

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        // 1. Buscamos el DTO simple en Redis (o en la BD si no está cacheado)
        UserCacheDto cachedData = getUserDataForCache(username);

        // 2. Reconstruimos los permisos al vuelo
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String auth : cachedData.getAuthorities()) {
            authorities.add(new SimpleGrantedAuthority(auth));
        }

        // 3. Devolvemos el objeto complejo que Spring Security exige (sin cachearlo)
        return new User(cachedData.getUsername(), cachedData.getPassword(), authorities);
    }

    @Cacheable(value = "userDetails", key = "#username")
    public UserCacheDto getUserDataForCache(String username) {
        UserEntity user = userRepository.findByUsernameAndActive(username, Boolean.TRUE)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<String> authorityStrings = new ArrayList<>();
        authorityStrings.add("ROLE_" + user.getRole().getName());

        List<PrivilegeEntity> privileges = rolePrivilegeRepository.findPrivilegesByRoleId(user.getRole().getId());
        for (PrivilegeEntity privilege : privileges) {
            authorityStrings.add(privilege.getName());
        }

        // Jackson convertirá este POJO simple a JSON sin problemas
        return new UserCacheDto(user.getUsername(), user.getPassword(), authorityStrings);
    }
}