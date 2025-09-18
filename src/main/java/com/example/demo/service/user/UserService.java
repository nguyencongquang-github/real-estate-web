package com.example.demo.service.user;


import com.example.demo.dto.user.UserDto;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();
    UserDto getUserInfo(Integer userId);
    Boolean deleteUser(Integer userId);
    UserDto createEmployee(UserDto userDto);
    UserDto updateUser(Integer userId, String username, String gender, String phone, String address, LocalDate dateOfBirth, MultipartFile imageUrl, String role, boolean isLocked);
    List<UserDto> searchUsers(String keyword);
}
