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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerStudent_ShouldSucceed_WhenUsernameAndEmailNotExist() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newstudent");
        request.setEmail("new@example.com");
        request.setPassword("secret");

        when(userRepository.existsByUsername("newstudent")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("encodedSecret");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> authService.registerStudent(request));
        verify(userRepository).save(argThat(user ->
                user.getUsername().equals("newstudent") &&
                        user.getEmail().equals("new@example.com") &&
                        user.getRole() == Role.STUDENT &&
                        user.isActive()
        ));
    }

    @Test
    void registerStudent_ShouldThrowException_WhenUsernameExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existing");
        when(userRepository.existsByUsername("existing")).thenReturn(true);
        assertThrows(RuntimeException.class, () -> authService.registerStudent(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void createPasswordResetTokenForUser_ShouldReturnToken() {
        User user = new User();
        user.setId(1L);
        String token = authService.createPasswordResetTokenForUser(user);
        assertNotNull(token);
        verify(tokenRepository).deleteByUser(user);
        verify(tokenRepository).save(any());
    }
}