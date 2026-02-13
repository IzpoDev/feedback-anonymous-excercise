package com.feedback.feedback.service.impl;

import com.feedback.feedback.exception.EntityNotFoundException;
import com.feedback.feedback.mapper.PrivilegeMapper;
import com.feedback.feedback.model.dto.PrivilegeRequestDto;
import com.feedback.feedback.model.dto.PrivilegeResponseDto;
import com.feedback.feedback.model.entity.PrivilegeEntity;
import com.feedback.feedback.repository.PrivilegeRepository;
import com.feedback.feedback.service.PrivilegeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrivilegeServiceImpl implements PrivilegeService {
    private final PrivilegeRepository privilegeRepository;

    @Override
    public PrivilegeResponseDto createPrivilege(PrivilegeRequestDto privilegeRequestDto) {
        if(!privilegeRepository.existByName(privilegeRequestDto.getName())){
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
    public void deletePrivilege(Long id) {
        PrivilegeEntity pv = privilegeRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("El privilegio con id " + id + " no existe")
        );
        pv.setActive(false);
        privilegeRepository.save(pv);
    }
}
