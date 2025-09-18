package com.example.demo.controller;

import com.example.demo.dto.user.*;
import com.example.demo.service.auth.AuthServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthServiceImpl authServiceImpl;

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterUserDto registerUserDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authServiceImpl.signup(registerUserDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> authenticate(@Valid @RequestBody LoginUserDto loginUserDTO) {
        return ResponseEntity.ok(authServiceImpl.authenticate(loginUserDTO));
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestBody VerifyUserDto verifyUserDTO) {
        return ResponseEntity.ok(authServiceImpl.verifyUser(verifyUserDTO));
    }

    @PostMapping("/resend")
    public ResponseEntity<String> resend(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        return ResponseEntity.ok(authServiceImpl.resendVerificationCode(email));
    }

//    @PostMapping("/refresh-token")
//    public ResponseEntity<TokenResponse> refreshToken(HttpServletRequest request) {
//        return ResponseEntity.ok(authServiceImpl.refreshAccessToken(request));
//    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        return ResponseEntity.ok(authServiceImpl.logout(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        return ResponseEntity.ok(authServiceImpl.forgotPassword(email));
    }

    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam("reset-token") String resetToken, @RequestBody ResetPasswordDto request) {
        return ResponseEntity.ok(authServiceImpl.resetPassword(resetToken, request));
    }

    @PutMapping("/change-password")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'REALTOR')")
    public ResponseEntity<String> changePassword(@RequestBody ResetPasswordDto request, Principal connectedUser) {
        return ResponseEntity.ok(authServiceImpl.changePassword(request, connectedUser));
    }


}
