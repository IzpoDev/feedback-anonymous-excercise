package com.feedback.feedback.modules.privilege.service.impl;

import com.feedback.feedback.common.exception.EntityNotFoundException;
import com.feedback.feedback.common.mapper.PrivilegeMapper;
import com.feedback.feedback.common.mapper.RolePrivilegeMapper;
import com.feedback.feedback.modules.privilege.controller.dto.PrivilegeRequestDto;
import com.feedback.feedback.modules.privilege.controller.dto.PrivilegeResponseDto;
import com.feedback.feedback.modules.privilege.controller.dto.RolePrivilegeResponseDto;
import com.feedback.feedback.modules.privilege.entity.PrivilegeEntity;
import com.feedback.feedback.modules.privilege.entity.RolePrivilegeEntity;
import com.feedback.feedback.modules.privilege.repository.PrivilegeRepository;
import com.feedback.feedback.modules.privilege.repository.RolePrivilegeRepository;
import com.feedback.feedback.modules.privilege.service.PrivilegeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrivilegeServiceImpl implements PrivilegeService {
    private final PrivilegeRepository privilegeRepository;
    private final RolePrivilegeRepository rolePrivilegeRepository;

    @Override
    public PrivilegeResponseDto createPrivilege(PrivilegeRequestDto privilegeRequestDto) {
        if(!privilegeRepository.existsByName(privilegeRequestDto.getName())){
            PrivilegeEntity pv = PrivilegeMapper.toEntity(privilegeRequestDto);
            pv.setActive(true);
            return PrivilegeMapper.toDto(privilegeRepository.save(pv));
        }
        throw new RuntimeException("El privilegio ya existe");
    }

    @Override
    public PrivilegeResponseDto getPrivilegeById(Long id) {
        return PrivilegeMapper.toDto(privilegeRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("El privilegio con id " + id + " no existe")
        ));
    }

    @Override
    public PrivilegeResponseDto updatePrivilege(Long id, PrivilegeRequestDto privilegeRequestDto) {
        PrivilegeEntity pv = privilegeRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("El privilegio con id " + id + " no existe")
        );
        pv.setName(privilegeRequestDto.getName());
        pv.setDescription(privilegeRequestDto.getDescription());
        return PrivilegeMapper.toDto(privilegeRepository.save(pv));
    }

    @Override
    public List<PrivilegeResponseDto> getAllPrivileges() {
        return PrivilegeMapper.toListDto(privilegeRepository.findAll());
    }

    @Override
    public List<RolePrivilegeResponseDto> getRolesWithPrivilege() {
        List<RolePrivilegeEntity> rolesWithPrivilege = rolePrivilegeRepository.findAll();
        return RolePrivilegeMapper.toListDto(rolesWithPrivilege);
    }

    @Override
    public void deletePrivilege(Long id) {
        PrivilegeEntity pv = privilegeRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("El privilegio con id " + id + " no existe")
        );
        pv.setActive(false);
        privilegeRepository.save(pv);
    }
}
