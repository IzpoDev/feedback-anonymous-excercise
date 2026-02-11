package com.feedback.feedback.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import com.feedback.feedback.model.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.feedback.feedback.repository.UserRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
@Getter
public class CustomerUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;
    private User userDetails;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findUsernameAndActive(username, Boolean.TRUE)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().getName());

        return new User(user.getUsername(), user.getPassword(), List.of(authority));
    }

}