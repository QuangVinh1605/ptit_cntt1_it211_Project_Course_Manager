package org.example.course_manager.service;

import org.example.course_manager.entity.RefreshToken;
import org.example.course_manager.entity.User;
import org.example.course_manager.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private User testUser;
    private final long refreshExpirationMs = 7 * 24 * 60 * 60 * 1000L; // 7 days

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setActive(true);

        // Set giá trị refreshExpirationMs vì @Value không hoạt động trong test unit
        ReflectionTestUtils.setField(refreshTokenService, "refreshExpirationMs", refreshExpirationMs);
    }

    // ==================== TEST createRefreshToken ====================
    @Test
    void createRefreshToken_ShouldDeleteOldTokenAndCreateNew() {
        // Given
        doNothing().when(refreshTokenRepository).deleteByUser(testUser);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> {
            RefreshToken token = invocation.getArgument(0);
            token.setId(100L);
            return token;
        });

        // When
        RefreshToken result = refreshTokenService.createRefreshToken(testUser);

        // Then
        assertNotNull(result);
        assertNotNull(result.getToken());
        assertTrue(result.getToken().length() > 10);
        assertNotNull(result.getExpiryDate());
        assertTrue(result.getExpiryDate().isAfter(Instant.now()));
        assertEquals(testUser, result.getUser());

        verify(refreshTokenRepository).deleteByUser(testUser);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void createRefreshToken_ShouldFlushAfterDelete() {
        // Kiểm tra rằng flush được gọi (dù verify không bắt được flush dễ dàng, nhưng có thể kiểm tra thứ tự)
        doNothing().when(refreshTokenRepository).deleteByUser(testUser);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(new RefreshToken());

        refreshTokenService.createRefreshToken(testUser);

        verify(refreshTokenRepository).deleteByUser(testUser);
        verify(refreshTokenRepository).flush();
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    // ==================== TEST verifyAndGet ====================
    @Test
    void verifyAndGet_ShouldReturnToken_WhenValid() {
        // Given
        String tokenValue = UUID.randomUUID().toString();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(tokenValue);
        refreshToken.setUser(testUser);
        refreshToken.setExpiryDate(Instant.now().plusMillis(3600000)); // 1 hour later

        when(refreshTokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(refreshToken));

        // When
        RefreshToken result = refreshTokenService.verifyAndGet(tokenValue);

        // Then
        assertNotNull(result);
        assertEquals(tokenValue, result.getToken());
        assertEquals(testUser, result.getUser());
        verify(refreshTokenRepository).findByToken(tokenValue);
        verify(refreshTokenRepository, never()).delete(any());
    }

    @Test
    void verifyAndGet_ShouldThrowException_WhenTokenNotFound() {
        String tokenValue = "nonexistent";
        when(refreshTokenRepository.findByToken(tokenValue)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> refreshTokenService.verifyAndGet(tokenValue));
        assertEquals("Refresh token không hợp lệ", exception.getMessage());
        verify(refreshTokenRepository).findByToken(tokenValue);
        verify(refreshTokenRepository, never()).delete(any());
    }

    @Test
    void verifyAndGet_ShouldDeleteAndThrowException_WhenTokenExpired() {
        String tokenValue = "expiredToken";
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(tokenValue);
        refreshToken.setUser(testUser);
        refreshToken.setExpiryDate(Instant.now().minusSeconds(1)); // expired

        when(refreshTokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(refreshToken));
        doNothing().when(refreshTokenRepository).delete(refreshToken);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> refreshTokenService.verifyAndGet(tokenValue));
        assertEquals("Refresh token đã hết hạn", exception.getMessage());
        verify(refreshTokenRepository).delete(refreshToken);
    }

    @Test
    void verifyAndGet_ShouldThrowException_WhenUserInactive() {
        testUser.setActive(false);
        String tokenValue = UUID.randomUUID().toString();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(tokenValue);
        refreshToken.setUser(testUser);
        refreshToken.setExpiryDate(Instant.now().plusMillis(3600000));

        when(refreshTokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(refreshToken));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> refreshTokenService.verifyAndGet(tokenValue));
        assertEquals("Tài khoản đã bị vô hiệu hóa", exception.getMessage());
        verify(refreshTokenRepository).findByToken(tokenValue);
        verify(refreshTokenRepository, never()).delete(any());
    }

    // ==================== TEST deleteByUser ====================
    @Test
    void deleteByUser_ShouldCallRepositoryDeleteByUser() {
        doNothing().when(refreshTokenRepository).deleteByUser(testUser);

        refreshTokenService.deleteByUser(testUser);

        verify(refreshTokenRepository).deleteByUser(testUser);
    }

    @Test
    void deleteByUser_ShouldHandleNullUserGracefully() {
        // Nếu user null, repository vẫn được gọi nhưng sẽ không xóa gì (tùy implement)
        doNothing().when(refreshTokenRepository).deleteByUser(null);

        assertDoesNotThrow(() -> refreshTokenService.deleteByUser(null));
        verify(refreshTokenRepository).deleteByUser(null);
    }
}