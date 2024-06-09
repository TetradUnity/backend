package com.tetradunity.server.services.shedulers;

import com.tetradunity.server.entities.RefreshToken;
import com.tetradunity.server.repositories.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service

public class DeleteExpiredScheduler {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Scheduled(fixedDelay = 300_000)
    public void deleteExpired(){
        LocalDateTime now = LocalDateTime.now();
        refreshTokenRepository.deleteByExpiryDateBefore(now);
    }
}