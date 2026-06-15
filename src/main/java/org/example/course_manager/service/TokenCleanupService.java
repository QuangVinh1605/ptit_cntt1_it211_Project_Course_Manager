package org.example.course_manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.course_manager.entity.TokenBlacklist;
import org.example.course_manager.repository.TokenBlacklistRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupService {
    private final TokenBlacklistRepository blacklistRepository;

    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanExpiredTokens() {
        Iterable<TokenBlacklist> all = blacklistRepository.findAll();
        int count = 0;
        for (TokenBlacklist token : all) {
            if (token.getExpiryDate().isBefore(Instant.now())) {
                blacklistRepository.delete(token);
                count++;
            }
        }
        log.info("Đã xóa {} token hết hạn khỏi blacklist", count);
    }
}