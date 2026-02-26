package com.feedback.feedback.config;

import com.feedback.feedback.model.entity.PrivilegeEntity;
import com.feedback.feedback.repository.RolePrivilegeRepository;
import lombok.RequiredArgsConstructor;
import com.feedback.feedback.model.entity.UserEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.feedback.feedback.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CustomerUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RolePrivilegeRepository rolePrivilegeRepository;

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsernameAndActive(username, Boolean.TRUE)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));

        // Consulta directa para obtener privilegios del role
        List<PrivilegeEntity> privileges = rolePrivilegeRepository.findPrivilegesByRoleId(user.getRole().getId());
        for (PrivilegeEntity privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege.getName()));
        }

        return new User(user.getUsername(), user.getPassword(), authorities);
    }

}