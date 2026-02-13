package com.feedback.feedback.controller;

import com.feedback.feedback.model.dto.PrivilegeRequestDto;
import com.feedback.feedback.model.dto.PrivilegeResponseDto;
import com.feedback.feedback.service.PrivilegeService;
import com.feedback.feedback.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/privileges")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class PrivilegeController {

    private final PrivilegeService privilegeService;
    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<PrivilegeResponseDto> createPrivilege(@RequestBody @Valid PrivilegeRequestDto privilegeRequestDto){
        PrivilegeResponseDto privilegeResponseDto = privilegeService.createPrivilege(privilegeRequestDto);
        return new ResponseEntity<>(privilegeResponseDto, HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<List<PrivilegeResponseDto>> getAllPrivileges(){
        List<PrivilegeResponseDto> privileges = privilegeService.getAllPrivileges();
        return new ResponseEntity<>(privileges, HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<PrivilegeResponseDto> getPrivilegeById(@PathVariable Long id){
        return new ResponseEntity<>(privilegeService.getPrivilegeById(id), HttpStatus.OK);
    }
    @PutMapping("/{id}")
    public ResponseEntity<PrivilegeResponseDto> updatePrivilege(@PathVariable Long id,@RequestBody @Valid PrivilegeRequestDto privilegeRequestDto){
        return new ResponseEntity<>(privilegeService.updatePrivilege(id, privilegeRequestDto), HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePrivilegeById(@PathVariable Long id){
        privilegeService.deletePrivilege(id);
        return new ResponseEntity<>("Privilegio con id " + id + " Eliminado ", HttpStatus.RESET_CONTENT);
    }
    @PostMapping("/assign")
    public ResponseEntity<String> assignPrivilegeFromRole(@RequestParam Long roleId,@RequestParam Long privilegeId){
        roleService.assignPrivilegeToRole(roleId, privilegeId);
        return new ResponseEntity<>("Privilegio con id " + privilegeId + " asignado al rol con id " + roleId, HttpStatus.OK);
    }
    @DeleteMapping("/assign")
    public ResponseEntity<String> removePrivilegeFromRole(@RequestParam Long roleId,@RequestParam Long privilegeId){
        roleService.removePrivilegeFromRole(roleId, privilegeId);
        return new ResponseEntity<>("Privilegio con id " + privilegeId + " removido del rol con id " + roleId, HttpStatus.OK);    }
}
