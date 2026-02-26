package com.feedback.feedback.service.impl;

import com.feedback.feedback.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import com.feedback.feedback.mapper.UserMapper;
import com.feedback.feedback.model.dto.UserRequestDto;
import com.feedback.feedback.model.dto.UserResponseDto;
import com.feedback.feedback.model.entity.UserEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.feedback.feedback.repository.RoleRepository;
import com.feedback.feedback.repository.UserRepository;
import com.feedback.feedback.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {

        UserEntity userEntity = UserMapper.toEntity(userRequestDto);
        if( !userRepository.existsByUsername(userEntity.getUsername())){
            userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
            userEntity.setActive(Boolean.TRUE);
            userEntity.setRole(roleRepository.findByName("OWNER").orElseThrow(
                    () -> new EntityNotFoundException("El rol OWNER no existe")
            ));
            userRepository.save(userEntity);
        } else {
            UserEntity userOld = userRepository.findByUsernameAndActiveFalse(userEntity.getUsername()).orElseThrow(() -> new RuntimeException("Username ya se encuentra en uso"));
            userEntity.setId(userOld.getId());
            userEntity.setActive(Boolean.TRUE);
            userEntity.setRole(userOld.getRole());
            userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
            userRepository.save(userEntity);
        }

        return UserMapper.toDto(userEntity);
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        return UserMapper.toDto(userRepository.getReferenceById(id));
    }

    @Override
    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {
        if(userRepository.existsByIdAndActiveTrue(id)){
            UserEntity user = userRepository.getReferenceById(id);
            user.setUsername(userRequestDto.getUsername());
            user.setEmail(userRequestDto.getEmail());
            userRepository.save(user);
            return UserMapper.toDto(user);
        }
        throw new RuntimeException("El usuario con id " + id + " no se encuentra activo o no existe");
    }

    @Override
    public List<UserResponseDto> getAllUsers() {

        return UserMapper.toListDto(userRepository.findAll());
    }

    @Override
    public List<UserResponseDto> getAllActiveUsers() {
        return UserMapper.toListDto(userRepository.findByActiveTrue());
    }

    @Override
    public void deleteUser(Long id) {
        if(!userRepository.existsByIdAndActiveTrue(id)){
            UserEntity userEntity = userRepository.getReferenceById(id);
            userEntity.setActive(Boolean.FALSE);
            userRepository.save(userEntity);
        }
        else throw new RuntimeException("El usuario con id " + id + " no se encuentra activo o no existe");
    }

    @Override
    public UserResponseDto registerAdmin(UserRequestDto userRequestDto) {

        UserEntity userEntity = UserMapper.toEntity(userRequestDto);
        if(!userRepository.existsByUsername(userEntity.getUsername())){
            userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
            userEntity.setActive(Boolean.TRUE);
            userEntity.setRole(roleRepository.findByName("ADMIN").orElseThrow(
                    ()-> new EntityNotFoundException("El rol ADMIN no existe")));
            userRepository.save(userEntity);
            return UserMapper.toDto(userEntity);
        } else {
            userEntity = userRepository.findByUsername(userEntity.getUsername()).orElseThrow(() -> new EntityNotFoundException("Username no se encuentra en la base de datos"));
            userEntity.setActive(Boolean.TRUE);
            userEntity.setRole(roleRepository.findByName("ADMIN").orElseThrow());
            return UserMapper.toDto(userRepository.save(userEntity));
        }
    }
}
