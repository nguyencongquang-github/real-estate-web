package com.example.demo.mapper;

import com.example.demo.dto.user.UserDto;
import com.example.demo.dto.user.UserResponseDTO;
import com.example.demo.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper extends EntityMapper<UserDto, User> {
    UserResponseDTO toUserResponseDTO(User user);
}
