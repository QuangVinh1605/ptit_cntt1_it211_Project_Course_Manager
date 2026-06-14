package org.example.course_manager.service;

import org.example.course_manager.constant.Role;
import org.example.course_manager.dto.request.RegisterRequest;
import org.example.course_manager.entity.User;
import org.example.course_manager.repository.PasswordResetTokenRepository;
import org.example.course_manager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private PasswordResetTokenRepository tokenRepository;
    @InjectMocks private AuthService authService;

    @Test
    void registerStudent_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPass");

        authService.registerStudent(request);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerStudent_UsernameAlreadyExists_ThrowsException() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existing");
        request.setEmail("new@example.com");

        when(userRepository.existsByUsername("existing")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> authService.registerStudent(request));
        verify(userRepository, never()).save(any());
    }
}