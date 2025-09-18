package com.example.demo.service.auth;


import com.example.demo.constant.PredefinedRole;
import com.example.demo.dto.user.*;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.InvalidDataException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.email.EmailServiceImpl;
import com.example.demo.service.jwt.JwtServiceImpl;
import com.example.demo.service.user.UserServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthServiceImpl {
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    UserRepository userRepository;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;
    AuthenticationManager authenticationManager;
    EmailServiceImpl emailServiceImpl;
    JwtServiceImpl jwtServiceImpl;
    UserDetailsService userDetailsService;
    UserMapper userMapper;
    UserServiceImpl userServiceImpl;

    public UserResponseDTO signup(RegisterUserDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent() && userRepository.findByEmail(request.getEmail()).get().isEnabled()) {
            throw new RuntimeException("Email đã được sử dụng");
        }

        HashSet<Role> roles = new HashSet<>();
        roleRepository.findByName(PredefinedRole.USER_ROLE).ifPresent(roles::add);

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .verificationCode(generateVerificationCode())
                .verificationCodeExpired(LocalDateTime.now().plusMinutes(1))
                .roles(roles)
                .enabled(false)
                .build();

        sendVerificationEmail(user);
        return userMapper.toUserResponseDTO(userRepository.save(user));
    }

    public TokenResponse authenticate(LoginUserDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .or(() -> userRepository.findByUsername(request.getEmail()))
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!user.isEnabled()) {
            throw new RuntimeException("Tài khoản của bạn không được kích hoạt. Vui lòng kích hoạt tài khoản của bạn.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu không đúng");
        }

        String role = user.getRoles().stream().findFirst().get().getName();
        String accessToken = jwtServiceImpl.generateToken(user);
        String refreshToken = jwtServiceImpl.generateRefreshToken(user);

        // Save token to database
        user.setAccessToken(accessToken);
        user.setRefreshToken(refreshToken);

        userRepository.save(user);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(role)
                .userId(user.getId())
                .build();
    }

    public String verifyUser(VerifyUserDto request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        log.info("Email: {}", request.getEmail());
        log.info("Verification code: {}", request.getVerificationCode());
        log.info("Optional user: {}", optionalUser.get().isEnabled());
        if (optionalUser.isPresent() && !optionalUser.get().isEnabled()) {
            User user = optionalUser.get();
            log.info("Verification code expired: {}", user.getVerificationCodeExpired());
            if (user.getVerificationCodeExpired().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Verification code has expired");
            }
            if (user.getVerificationCode().equals(request.getVerificationCode())) {
                user.setEnabled(true);
                user.setVerificationCode(null);
                user.setVerificationCodeExpired(null);
                userRepository.save(user);
            } else {
                throw new RuntimeException("Mã xác thực không đúng");
            }
        } else {
            throw new NotFoundException("Tài khoản không tồn tại");
        }

        return "Tài khoản đã được kích hoạt thành công";
    }

    // Resend verification code
    public String resendVerificationCode(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (user.isEnabled()) {
                throw new RuntimeException("Tài khoản đã được kích hoạt");
            }

            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpired(LocalDateTime.now().plusMinutes(1));
            userRepository.save(user);

            sendVerificationEmail(user);
        } else {
            throw new NotFoundException("Tài khoản không tồn tại");
        }

        return "Verification code sent successfully";
    }

    public String logout(HttpServletRequest request) {
        String refreshToken = request.getHeader("x-token");
        if (refreshToken == null) {
            throw new RuntimeException("Token không được để trống");
        }

        // Extract username from token
        final String username = jwtServiceImpl.extractUsername(refreshToken);
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Tài khoản không tồn tại");
        }

        User user = optionalUser.get();
        user.setAccessToken(null);
        userRepository.save(user);

        return "Logout successfully";
    }

    private String sendVerificationEmail(User user) {
        String subject = "VERIFICATION ACCOUNT";
        String verificationCode = "VERIFICATION CODE " + user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">VERIFICATION CODE:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";
        try {
            emailServiceImpl.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Gửi email xác thực không thành công", e);
        }

        return "Mã xác thực đã gửi thành công";
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    // Refresh access token when it's expired
//    public TokenResponse refreshAccessToken(HttpServletRequest request) {
//        String refreshToken = request.getHeader("x-token");
//        if (refreshToken == null) {
//            throw new RuntimeException("Token không được để trống");
//        }
//        final String username = jwtServiceImpl.extractUsername(refreshToken);
//        Optional<User> user = userRepository.findByUsername(username);
//
//        if (!jwtServiceImpl.isTokenValid(refreshToken, user.get())) {
//            throw new RuntimeException("Token không hợp lệ");
//        }
//
//        String accessToken = jwtServiceImpl.generateToken(user.get());
//        return TokenResponse.builder()
//                .accessToken(accessToken)
//                .refreshToken(refreshToken)
//                .role(user.get().getRoles().stream().findFirst().get().getName())
//                .userId(user.get().getId())
//                .build();
//    }

    public String forgotPassword(String email) {
        // Check email exists or not
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Tài khoản không tồn tại");
        }

        User user = optionalUser.get();

        // User is active or inactive
        if (!user.isEnabled()) {
            throw new InvalidDataException("Tài khoản không được kích hoạt");
        }

        // Generate reset token
        String resetToken = jwtServiceImpl.generateResetPasswordToken(user);

        // Send email reset link
        String subject = "RESET PASSWORD";
        String resetLink = "http://localhost:8080/reset-password?reset-token=" + resetToken;
        String htmlMessage = "<html>"
                + "<body>"
                + "<p>Dear " + user.getUsername() + ",</p>"
                + "<p>We received a request to reset your password. Click the link below to set a new password:</p>"
                + "<a href=\"" + resetLink + "\">ĐẶT LẠI MẬT KHẨU</a>"
                + "<p>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.</p>"
                + "</body>"
                + "</html>";

        try {
            emailServiceImpl.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to send reset password email", e);
        }

        return "Liên kết đặt lại mật khẩu đã gửi thành công";
    }

    @Transactional
    public String resetPassword(String resetToken, ResetPasswordDto request) {
        log.info("========== Reset password ==========");

        // Check token and return user
        User user = isValidUserByToken(resetToken);

        // Check new password and confirm password
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu và mật khẩu xác nhận không khớp");
        }

        // Set new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        Set<Role> roles = user.getRoles();
        user.setRoles(roles);
        userRepository.save(user);

        return "Mật khẩu đã được đặt lại thành công";
    }

    // Change password with token
    public String changePassword(ResetPasswordDto request, Principal connectedUser) {
        log.info("========= Change password ==========");

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // Check current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidDataException("Mật khẩu cũ không đúng");
        }

        // Check new password and confirm password
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new InvalidDataException("Mật khẩu và mật khẩu xác nhận không khớp");
        }

        // Set new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return "Mật khẩu đã được thay đổi thành công";
    }

    // Check token and return user
    private User isValidUserByToken(String token) {
        final String username = jwtServiceImpl.extractUsername(token);
        var user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));

        if (!jwtServiceImpl.isTokenValid(token, user)) {
            throw new RuntimeException("Token không hợp lệ");
        }

        return user;
    }
}