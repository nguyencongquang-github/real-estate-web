package com.example.demo.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Validated
public class RegisterUserDto {
    @Size(min = 5, message = "Username must be at least 5 characters")
    @NotBlank(message = "Username is required")
    String username;

    @Email(message = "Email invalid format")
    @NotBlank(message = "Email is required")
    String email;

    @Size(min = 8, message = "Password must be at least 8 characters")
    @NotBlank(message = "Password is required")
    String password;
}
