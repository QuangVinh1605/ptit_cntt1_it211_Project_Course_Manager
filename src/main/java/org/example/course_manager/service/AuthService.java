package org.example.course_manager.service;

import lombok.RequiredArgsConstructor;
import org.example.course_manager.constant.Role;
import org.example.course_manager.dto.request.RegisterRequest;
import org.example.course_manager.entity.PasswordResetToken;
import org.example.course_manager.entity.User;
import org.example.course_manager.repository.PasswordResetTokenRepository;
import org.example.course_manager.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public void registerStudent(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username da ton tai");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email da duoc dang ky");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.STUDENT);
        userRepository.save(user);
    }

    public String createPasswordResetTokenForUser(User user) {
        // Xóa token cũ nếu có
        passwordResetTokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusHours(1);
        PasswordResetToken resetToken = new PasswordResetToken(token, user, expiry);
        passwordResetTokenRepository.save(resetToken);

        return token;
    }
}