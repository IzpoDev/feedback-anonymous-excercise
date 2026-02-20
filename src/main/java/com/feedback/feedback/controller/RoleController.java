package com.feedback.feedback.controller;

import com.feedback.feedback.model.dto.RoleRequestDto;
import com.feedback.feedback.model.dto.RoleResponseDto;
import com.feedback.feedback.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

        private final RoleService roleService;

        @PostMapping
        public ResponseEntity<RoleResponseDto> createRole(@RequestBody @Valid RoleRequestDto roleRequestDto){
            RoleResponseDto roleResponseDto = roleService.createRole(roleRequestDto);
            return new ResponseEntity<>(roleResponseDto, HttpStatus.CREATED);
        }
        @GetMapping
        public ResponseEntity<List<RoleResponseDto>> getAllRoles(){
            return new ResponseEntity<>(roleService.getAllRoles(), HttpStatus.OK);
        }
        @GetMapping("/{id}")
        public ResponseEntity<RoleResponseDto> getRoleById(@PathVariable Long id){
            return new ResponseEntity<>(roleService.getRoleById(id), HttpStatus.OK);
        }
        @DeleteMapping("/{id}")
        public ResponseEntity<String> deleteRoleById(@PathVariable Long id){
            roleService.deleteRole(id);
            return new ResponseEntity<>("Rol con id " + id + " Eliminado ", HttpStatus.OK);
        }
        @PutMapping("/{id}")
        public ResponseEntity<RoleResponseDto> updateRole(@PathVariable Long id,@RequestBody @Valid RoleRequestDto roleRequestDto){
            return new ResponseEntity<>(roleService.updateRole(id, roleRequestDto), HttpStatus.OK);
        }

}
