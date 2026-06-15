package org.example.course_manager.service;

import org.example.course_manager.entity.User;
import org.example.course_manager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedOldPass");
        testUser.setActive(true);
    }

    @Test
    void findByUsername_ShouldReturnUser_WhenExistsAndActive() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        User found = userService.findByUsername("testuser");
        assertNotNull(found);
        assertEquals("testuser", found.getUsername());
    }

    @Test
    void findByUsername_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.findByUsername("unknown"));
    }

    @Test
    void findByUsername_ShouldThrowException_WhenUserInactive() {
        testUser.setActive(false);
        when(userRepository.findByUsername("inactive")).thenReturn(Optional.of(testUser));
        assertThrows(RuntimeException.class, () -> userService.findByUsername("inactive"));
    }

    @Test
    void changePassword_ShouldSucceed_WhenOldPasswordMatches() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPass", "encodedOldPass")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");
        doNothing().when(userRepository).save(any(User.class));

        assertDoesNotThrow(() -> userService.changePassword("testuser", "oldPass", "newPass"));
        verify(userRepository).save(testUser);
        assertEquals("encodedNewPass", testUser.getPassword());
    }

    @Test
    void changePassword_ShouldThrowException_WhenOldPasswordWrong() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPass", "encodedOldPass")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> userService.changePassword("testuser", "wrongPass", "newPass"));
        verify(userRepository, never()).save(any());
    }
}