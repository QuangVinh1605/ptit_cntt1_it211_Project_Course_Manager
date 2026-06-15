package org.example.course_manager.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.course_manager.dto.request.LoginRequest;
import org.example.course_manager.dto.request.RegisterRequest;
import org.example.course_manager.entity.User;
import org.example.course_manager.repository.PasswordResetTokenRepository;
import org.example.course_manager.repository.UserRepository;
import org.example.course_manager.security.JwtUtils;
import org.example.course_manager.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private JwtUtils jwtUtils;
    @MockBean
    private RefreshTokenService refreshTokenService;
    @MockBean
    private BlacklistService blacklistService;
    @MockBean
    private UserService userService;
    @MockBean
    private PasswordResetTokenRepository tokenRepository;
    @MockBean
    private EmailService emailService;
    @MockBean
    private UserRepository userRepository; // cần thêm nếu AuthController dùng

    @Test
    void register_ShouldReturnCreated() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("test");
        request.setEmail("test@ex.com");
        request.setPassword("pass123");
        doNothing().when(authService).registerStudent(any(RegisterRequest.class));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Đăng ký thành công"));
    }

    @Test
    void login_ShouldReturnJwtResponse() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("user");
        loginRequest.setPassword("pass");

        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(auth.getName()).thenReturn("user");
        User user = new User();
        user.setUsername("user");
        user.setActive(true);
        user.setRole(org.example.course_manager.constant.Role.STUDENT);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(jwtUtils.generateJwtToken(eq("user"), anyString())).thenReturn("access.token");
        when(refreshTokenService.createRefreshToken(any(User.class))).thenReturn(new RefreshToken(null, user, "refresh.token", Instant.now().plusSeconds(3600)));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access.token"))
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    // Có thể test forgot-password, reset-password...

    private String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}