package com.example.demo.service.auth;

import com.example.demo.dto.user.*;
import jakarta.servlet.http.HttpServletRequest;

import java.security.Principal;

public interface AuthService {
    UserResponseDTO signup(RegisterUserDto input);
    TokenResponse authenticate(LoginUserDto input);
    String verifyUser(VerifyUserDto input);
    String resendVerificationCode(String email);
    String logout(HttpServletRequest request);
    TokenResponse refreshAccessToken(HttpServletRequest request);
    String forgotPassword(String email);
    String resetPassword(String resetToken, ResetPasswordDto request);
    String changePassword(ResetPasswordDto request, Principal connectedUser);
}
