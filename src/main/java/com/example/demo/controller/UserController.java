package com.example.demo.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.user.UserDto;
import com.example.demo.entity.User;
import com.example.demo.service.user.UserServiceImpl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserServiceImpl userServiceImpl;

    @PreAuthorize("hasAnyAuthority('USER', 'REALTOR', 'ADMIN')")
    @GetMapping("/my-info")
    public ApiResponse<UserDto> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ApiResponse.<UserDto>builder()
                .code(HttpStatus.OK.value())
                .message("Get current user successfully")
                .data(userServiceImpl.getUserInfo(currentUser.getId()))
                .build();
    }

    @GetMapping("/get-all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<List<UserDto>> getAllUsers() {
        return ApiResponse.<List<UserDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Get all users successfully")
                .data(userServiceImpl.getAllUsers())
                .build();
    }

    @GetMapping("/get-user/{userId}")
    public ApiResponse<UserDto> getUser(@PathVariable Integer userId) {
        return ApiResponse.<UserDto>builder()
                .code(HttpStatus.OK.value())
                .message("Get user successfully")
                .data(userServiceImpl.getUserInfo(userId))
                .build();
    }

    @DeleteMapping("/delete/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<Boolean> deleteUser(@PathVariable Integer userId) {
        return ApiResponse.<Boolean>builder()
                .code(HttpStatus.OK.value())
                .message("Delete user successfully")
                .data(userServiceImpl.deleteUser(userId))
                .build();
    }

    @PutMapping("/update/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'REALTOR')")
    public ApiResponse<UserDto> updateUser(@PathVariable Integer userId,
                                           @RequestParam String username,
                                           @RequestParam(required = false) String gender,
                                           @RequestParam(required = false) String phone,
                                           @RequestParam(required = false) String address,
                                           @RequestParam(required = false) LocalDate dateOfBirth,
                                           @RequestParam(required = false) MultipartFile imageUrl,
                                           @RequestParam(required = false) String role,
                                           @RequestParam(required = false) boolean isLocked) {
        return ApiResponse.<UserDto>builder()
                .code(HttpStatus.OK.value())
                .message("Update user successfully")
                .data(userServiceImpl.updateUser(userId, username, gender, phone, address, dateOfBirth, imageUrl, role, isLocked))
                .build();
    }

    @PostMapping("/create-employee")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<UserDto> createEmployee(@RequestBody UserDto userDto) {
        return ApiResponse.<UserDto>builder()
                .code(HttpStatus.CREATED.value())
                .message("Create employee successfully")
                .data(userServiceImpl.createEmployee(userDto))
                .build();
    }


    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<List<UserDto>> searchUsers(@RequestParam String keyword) {
        return ApiResponse.<List<UserDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Search users successfully")
                .data(userServiceImpl.searchUsers(keyword))
                .build();
    }
}
