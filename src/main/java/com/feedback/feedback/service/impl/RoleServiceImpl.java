package com.feedback.feedback.service.impl;

import com.feedback.feedback.model.entity.PrivilegeEntity;
import com.feedback.feedback.model.entity.RolePrivilegeEntity;
import com.feedback.feedback.repository.PrivilegeRepository;
import com.feedback.feedback.repository.RolePrivilegeRepository;
import lombok.RequiredArgsConstructor;
import com.feedback.feedback.mapper.RoleMapper;
import com.feedback.feedback.model.dto.RoleRequestDto;
import com.feedback.feedback.model.dto.RoleResponseDto;
import com.feedback.feedback.model.entity.RoleEntity;
import org.springframework.stereotype.Service;
import com.feedback.feedback.repository.RoleRepository;
import com.feedback.feedback.service.RoleService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final PrivilegeRepository privilegeRepository;
    private final RolePrivilegeRepository rolePrivilegeRepository;
    private final RoleRepository roleRepository;
    @Override
    public RoleResponseDto createRole(RoleRequestDto roleRequestDto) {
        if(!roleRepository.existsByName(roleRequestDto.getName())){
            RoleEntity roleEntity = RoleMapper.toEntity(roleRequestDto);
            roleEntity.setActive(true);
            roleRepository.save(roleEntity);
            return RoleMapper.toDto(roleEntity);
        }
        throw new RuntimeException("El rol ya existe");
    }

    @Override
    public RoleResponseDto getRoleById(Long id) {
        return RoleMapper.toDto(roleRepository.getReferenceById(id));
    }

    @Override
    public RoleResponseDto updateRole(Long id, RoleRequestDto roleRequestDto) {
        if(roleRepository.existsByName(roleRequestDto.getName())){
            RoleEntity roleEntity = roleRepository.getReferenceById(id);
            roleEntity.setName(roleRequestDto.getName());
            roleEntity.setDescription(roleRequestDto.getDescription());
            roleRepository.save(roleEntity);
            return RoleMapper.toDto(roleEntity);
        }
        throw new RuntimeException("El rol con id " + id + " no existe");
    }

    @Override
    public List<RoleResponseDto> getAllRoles() {
        return RoleMapper.toListDto(roleRepository.findAll());
    }

    @Override
    public void deleteRole(Long id) {
        if(roleRepository.existsById(id)){
            RoleEntity roleEntity = roleRepository.getReferenceById(id);
            roleEntity.setActive(false);
            roleRepository.save(roleEntity);
        }
        throw new RuntimeException("El rol con id " + id + " no existe");

    }

    @Override
    public void assignPrivilegeToRole(Long roleId, Long privilegeId) {

        RoleEntity role = roleRepository.findById(roleId).orElseThrow();
        PrivilegeEntity privilege = privilegeRepository.findById(privilegeId).orElseThrow();
        if (rolePrivilegeRepository.findByRoleAndPrivilegeAndActive(role, privilege, true).isEmpty()) {
            RolePrivilegeEntity rp = new RolePrivilegeEntity();
            rp.setRole(role);
            rp.setPrivilege(privilege);
            rp.setActive(true);
            rolePrivilegeRepository.save(rp);
        }
    }

    @Override
    public void removePrivilegeFromRole(Long roleId, Long privilegeId) {
        RoleEntity role = roleRepository.findById(roleId).orElseThrow();
        PrivilegeEntity privilege = privilegeRepository.findById(privilegeId).orElseThrow();
        RolePrivilegeEntity rp = rolePrivilegeRepository.findByRoleAndPrivilegeAndActive(role, privilege, true).orElseThrow();
        rp.setActive(false);
        rolePrivilegeRepository.save(rp);

    }
}
