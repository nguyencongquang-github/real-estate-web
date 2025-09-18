package com.example.demo.dto.user;

import com.example.demo.entity.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponseDTO {
    Integer id;
    String username;
    String email;
    String phone;
    String address;
    String avatar;
    String gender;
    boolean enabled;
    boolean accountLocked;
    String verificationCode;
    LocalDateTime verificationCodeExpired;
    LocalDate dateOfBirth;
    LocalDateTime createDate;
    LocalDateTime updateDate;
    Set<Role> roles = new HashSet<>();
}
