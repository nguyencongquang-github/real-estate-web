package com.example.demo.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ResetPasswordDto implements Serializable {
    private String token;
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}
