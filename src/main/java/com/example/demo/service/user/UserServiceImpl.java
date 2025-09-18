package com.example.demo.service.user;

import com.example.demo.constant.PredefinedRole;
import com.example.demo.dto.user.UserDto;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.aws.AwsS3ServiceImpl;
import com.example.demo.service.email.EmailService;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    EmailService emailService;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;
    AwsS3ServiceImpl awsS3Service;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public UserDto getUserInfo(Integer userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        return userMapper.toDto(userRepository.findById(userId).get());
    }



    @Override
    @Transactional
    public Boolean deleteUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found");
        }
//        // Xóa người dùng
        userRepository.deleteById(userId);

        // Flush để đảm bảo thay đổi đã được commit
        userRepository.flush();

        // Kiểm tra lại xem người dùng có bị xóa thành công hay không
        return !userRepository.existsById(userId);
    }

    @Override
    @Transactional
    public UserDto createEmployee(UserDto userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        HashSet<Role> roles = new HashSet<>();
        roleRepository.findByName(PredefinedRole.USER_ROLE).ifPresent(roles::add);

        String randomPassword = generateRandomPassword();

        User user = User.builder()
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .roles(roles)
                .enabled(true)
                .build();

        sendAccountDetailsEmail(user, randomPassword);
        user.setPassword(passwordEncoder.encode(randomPassword));
        userRepository.save(user);

        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(Integer userId, String username, String gender, String phone, String address, LocalDate dateOfBirth, MultipartFile imageUrl, String role, boolean isLocked) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found"));

        boolean isEnable = authenticatedUser.isEnabled();

        boolean isAdmin = authenticatedUser.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ADMIN"));

        if (!isEnable && !isAdmin && !authenticatedUser.getId().equals(userId)) {
            throw new IllegalArgumentException("You can only update your own account");
        }
        log.info("Updating user with ID: {}", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setUsername(username);
        user.setGender(gender);
        user.setPhone(phone);
        user.setAddress(address);
        user.setDateOfBirth(dateOfBirth);
        user.setAccountLocked(isLocked);

        if (imageUrl != null) {
            String imageUrlStr = awsS3Service.saveImageToS3(imageUrl);
            user.setImageUrl(imageUrlStr);
        }

        if (role != null) {
            Role newRole = roleRepository.findByName(role).orElseThrow(() -> new IllegalArgumentException("Role not found"));
            user.getRoles().clear();
            user.getRoles().add(newRole);
        }

        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> searchUsers(String keyword) {
        List<User> users = userRepository.findByUsernameContainingOrEmailContaining(keyword, keyword);
        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    private String generateRandomPassword() {
        Random random = new Random();
        int length = 8;
        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            password.append(random.nextInt(10));
        }

        return password.toString();
    }


    private void sendAccountDetailsEmail(User user, String password) {
        String subject = "Your Employee Account Details";
        String message = "<html>"
                + "<body style=\"font-family: Arial, sans-serif; background-color: #f5f5f5; padding: 20px;\">"
                + "<div style=\"max-width: 600px; margin: auto; background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);\">"
                + "<h2 style=\"color: #333;\">Hello, " + user.getUsername() + "!</h2>"
                + "<p style=\"font-size: 16px; color: #555;\">Here are your account details:</p>"

                // Thông tin tài khoản
                + "<div style=\"background-color: #f9f9f9; padding: 15px; border-radius: 5px; margin: 15px 0;\">"
                + "<p style=\"font-size: 16px; color: #333;\"><strong>Username:</strong> " + user.getUsername() + "</p>"
                + "<p style=\"font-size: 16px; color: #333;\"><strong>Password:</strong> " + password + "</p>"
                + "</div>"

                // Thông báo yêu cầu giữ bảo mật
                + "<p style=\"font-size: 16px; color: red; font-weight: bold;\">Please enter your username/email and password to log in and keep your account information secure.</p>"

                + "<p style=\"font-size: 16px; color: #555;\">Best regards,<br>My Team</p>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, message);
        } catch (UnsupportedEncodingException | MessagingException e) {
            throw new RuntimeException("Failed to send account details email", e);
        }
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public void updateUserRole(User user, String roleRealtor) {
        Role role = roleRepository.findByName(roleRealtor)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        user.getRoles().clear();
        user.getRoles().add(role);
        userRepository.save(user);
    }
}
