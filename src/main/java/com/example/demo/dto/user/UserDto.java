package com.example.demo.dto.user;

import com.example.demo.entity.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    Integer id;
    String username;
    String email;
    String phone;
    String imageUrl;
    String address;
    boolean enabled;
    boolean accountLocked;
    LocalDate dateOfBirth;
    String verificationCode;
    LocalDateTime verificationCodeExpired;
    String accessToken;
    String refreshToken;
    Set<Role> roles;
}
