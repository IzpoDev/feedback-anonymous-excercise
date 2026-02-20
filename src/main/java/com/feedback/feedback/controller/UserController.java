package com.feedback.feedback.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.feedback.feedback.model.dto.UserRequestDto;
import com.feedback.feedback.model.dto.UserResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.feedback.feedback.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody @Valid UserRequestDto userRequestDto){
        UserResponseDto userResponseDto = userService.createUser(userRequestDto);
        return new ResponseEntity<>(userResponseDto, HttpStatus.CREATED);
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id){
        UserResponseDto userResponseDto = userService.getUserById(id);
        return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers(){
        List<UserResponseDto> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    @GetMapping("/active")
    public ResponseEntity<List<UserResponseDto>> getAllActiveUsers(){
        List<UserResponseDto> activeUsers = userService.getAllActiveUsers();
        return new ResponseEntity<>(activeUsers, HttpStatus.OK);
    }
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id,@RequestBody @Valid UserRequestDto userRequestDto){
        UserResponseDto user = userService.updateUser(id, userRequestDto);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long id){
        userService.deleteUser(id);
        return new ResponseEntity<>("Ususario con id " + id + " Eliminado ", HttpStatus.OK);
    }
    @PostMapping("/admin")
    public ResponseEntity<UserResponseDto> registerAdmin(@RequestBody @Valid UserRequestDto userRequestDto){
        return new ResponseEntity<>(userService.registerAdmin(userRequestDto), HttpStatus.CREATED);
    }
}
