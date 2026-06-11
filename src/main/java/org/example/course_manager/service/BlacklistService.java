package org.example.course_manager.service;

import lombok.RequiredArgsConstructor;
import org.example.course_manager.entity.TokenBlacklist;
import org.example.course_manager.repository.TokenBlacklistRepository;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class BlacklistService {
    private final TokenBlacklistRepository blacklistRepository;

    public void blacklistToken(String token, Instant expiryDate) {
        TokenBlacklist entry = new TokenBlacklist(token, expiryDate);
        blacklistRepository.save(entry);
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistRepository.existsById(token);
    }
}