package com.app.oauth.service;

import com.app.oauth.exception.RefreshTokenExpiredException;
import com.app.oauth.model.RefreshToken;
import com.app.oauth.model.Role;
import com.app.oauth.model.User;
import com.app.oauth.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private User testUser;
    private RefreshToken testRefreshToken;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("hashed_password_123");
        testUser.setRole(Role.USER);

        testRefreshToken = new RefreshToken();
        testRefreshToken.setId(1L);
        testRefreshToken.setToken("test-refresh-token");
        testRefreshToken.setUser(testUser);
        testRefreshToken.setCreatedAt(LocalDateTime.now());
        testRefreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));

        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenDurationMs", 604800000L);
    }

    @Test
    void testCreateRefreshToken() {
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(testRefreshToken);

        RefreshToken createdToken = refreshTokenService.createRefreshToken(testUser);

        assertNotNull(createdToken);
        assertEquals(testUser.getId(), createdToken.getUser().getId());
        assertNotNull(createdToken.getToken());
        assertTrue(createdToken.getToken().length() > 0);
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    void testFindByToken() {
        when(refreshTokenRepository.findByToken("test-refresh-token"))
                .thenReturn(Optional.of(testRefreshToken));

        Optional<RefreshToken> found = refreshTokenService.findByToken("test-refresh-token");

        assertTrue(found.isPresent());
        assertEquals("test-refresh-token", found.get().getToken());
    }

    @Test
    void testFindByToken_NotFound() {
        when(refreshTokenRepository.findByToken("invalid-token"))
                .thenReturn(Optional.empty());

        Optional<RefreshToken> found = refreshTokenService.findByToken("invalid-token");

        assertFalse(found.isPresent());
    }

    @Test
    void testIsExpired_TokenNotExpired() {
        testRefreshToken.setExpiryDate(LocalDateTime.now().plusDays(1));

        boolean isExpired = refreshTokenService.isExpired(testRefreshToken);

        assertFalse(isExpired);
    }

    @Test
    void testIsExpired_TokenExpired() {
        testRefreshToken.setExpiryDate(LocalDateTime.now().minusHours(1));

        boolean isExpired = refreshTokenService.isExpired(testRefreshToken);

        assertTrue(isExpired);
    }

    @Test
    void testVerifyExpiration_ValidToken() {
        testRefreshToken.setExpiryDate(LocalDateTime.now().plusDays(1));

        RefreshToken verified = refreshTokenService.verifyExpiration(testRefreshToken);

        assertNotNull(verified);
        assertEquals(testRefreshToken.getId(), verified.getId());
    }

    @Test
    void testVerifyExpiration_ExpiredToken() {
        testRefreshToken.setExpiryDate(LocalDateTime.now().minusHours(1));

        assertThrows(RefreshTokenExpiredException.class, () -> {
            refreshTokenService.verifyExpiration(testRefreshToken);
        });

        verify(refreshTokenRepository, times(1)).delete(testRefreshToken);
    }

    @Test
    void testRevokeToken() {
        when(refreshTokenRepository.findByToken("test-refresh-token"))
                .thenReturn(Optional.of(testRefreshToken));

        refreshTokenService.revokeToken("test-refresh-token");

        verify(refreshTokenRepository, times(1)).delete(testRefreshToken);
    }

    @Test
    void testRevokeToken_TokenNotFound() {
        when(refreshTokenRepository.findByToken("nonexistent-token"))
                .thenReturn(Optional.empty());

        refreshTokenService.revokeToken("nonexistent-token");

        verify(refreshTokenRepository, never()).delete(any());
    }

    @Test
    void testDeleteByUser() {
        refreshTokenService.deleteByUser(testUser);

        verify(refreshTokenRepository, times(1)).deleteByUser(testUser);
    }
}