package com.tetradunity.server.services;

import com.tetradunity.server.projections.StartSubjectRemind;
import com.tetradunity.server.repositories.PasswordRecoveryRequestRepository;
import com.tetradunity.server.repositories.RefreshTokenRepository;
import com.tetradunity.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service

public class Scheduler {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private PasswordRecoveryRequestRepository passwordRecoveryRequestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MailService mailService;

    @Scheduled(fixedDelay = 300_000)
    public void deleteExpired(){
        LocalDateTime now = LocalDateTime.now();
        refreshTokenRepository.deleteByExpiryDateBefore(now);
        passwordRecoveryRequestRepository.deleteByExpiryDateBefore();
    }

    @Scheduled(fixedDelay = 600_000)
    public void subjectStart(){
        List<StartSubjectRemind> users = userRepository.findUserRemind();
        for(StartSubjectRemind user : users){
            mailService.sendStartSubjectRemind(user.getEmail(), user.getFirst_name(), user.getTitle());
        }
    }
}