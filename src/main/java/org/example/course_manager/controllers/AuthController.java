package org.example.course_manager.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.course_manager.dto.request.LoginRequest;
import org.example.course_manager.dto.request.RefreshTokenRequest;
import org.example.course_manager.dto.request.RegisterRequest;
import org.example.course_manager.dto.response.ApiResponse;
import org.example.course_manager.dto.response.JwtResponse;
import org.example.course_manager.entity.PasswordResetToken;
import org.example.course_manager.entity.RefreshToken;
import org.example.course_manager.entity.User;
import org.example.course_manager.repository.PasswordResetTokenRepository;
import org.example.course_manager.repository.UserRepository;
import org.example.course_manager.security.JwtUtils;
import org.example.course_manager.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final BlacklistService blacklistService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse register(@Valid @RequestBody RegisterRequest request) {
        authService.registerStudent(request);
        return new ApiResponse(true, "Đăng ký thành công");
    }

    @PostMapping("/login")
    public JwtResponse login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));

        if (!user.isActive()) {
            throw new RuntimeException("Tài khoản đã bị vô hiệu hóa");
        }

        String accessToken = jwtUtils.generateJwtToken(user.getUsername(), user.getRole().name());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        return new JwtResponse(accessToken, refreshToken.getToken(), "Bearer");
    }

    @PostMapping("/refresh")
    public JwtResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.verifyAndGet(request.getRefreshToken());
        User user = refreshToken.getUser();
        String newAccessToken = jwtUtils.generateJwtToken(user.getUsername(), user.getRole().name());
        return new JwtResponse(newAccessToken, refreshToken.getToken(), "Bearer");
    }

    @PostMapping("/logout")
    public ApiResponse logout(HttpServletRequest request) {
        String jwt = parseJwt(request);
        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
            Instant expiry = jwtUtils.getExpirationDate(jwt).toInstant();
            blacklistService.blacklistToken(jwt, expiry);
            String username = jwtUtils.getUserNameFromJwtToken(jwt);
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                refreshTokenService.deleteByUser(user);
            }
        }
        return new ApiResponse(true, "Đăng xuất thành công");
    }

    @PostMapping("/forgot-password")
    public ApiResponse forgotPassword(@RequestParam String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại trong hệ thống"));
        String token = authService.createPasswordResetTokenForUser(user);

        // Gửi email chứa token
        emailService.sendPasswordResetToken(user.getEmail(), token);

        return new ApiResponse(true, "Email đặt lại mật khẩu đã được gửi. Vui lòng kiểm tra hộp thư.");
    }

    @PostMapping("/reset-password")
    public ApiResponse resetPassword(@RequestParam String token,
                                     @RequestParam String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token không hợp lệ hoặc đã được sử dụng"));
        if (resetToken.getExpiryDate().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Token đã hết hạn. Vui lòng yêu cầu gửi lại.");
        }
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(resetToken);
        return new ApiResponse(true, "Đặt lại mật khẩu thành công. Bạn có thể đăng nhập với mật khẩu mới.");
    }


    private String parseJwt(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}