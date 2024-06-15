package com.tetradunity.server.services;

import com.tetradunity.server.projections.ConferenceRemindProjection;
import com.tetradunity.server.projections.StartSubjectRemindProjection;
import com.tetradunity.server.repositories.ConferenceRepository;
import com.tetradunity.server.repositories.PasswordRecoveryRequestRepository;
import com.tetradunity.server.repositories.RefreshTokenRepository;
import com.tetradunity.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
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
    @Autowired
    private ConferenceRepository conferenceRepository;

    @Scheduled(fixedDelay = 300_000)
    public void deleteExpired(){
        Instant now = Instant.now();
        refreshTokenRepository.deleteByExpiryDateBefore(now);
        passwordRecoveryRequestRepository.deleteByExpiryDateBefore();
        for(ConferenceRemindProjection projection : conferenceRepository.conferencesRemind()){
            for(String email : projection.getStudent_emails()){
                mailService.sendConferenceRemind(email, projection.getSubject_title());
            }
        }
    }

    @Scheduled(fixedDelay = 600_000)
    public void subjectStart(){
        List<StartSubjectRemindProjection> users = userRepository.findUserRemind();
        for(StartSubjectRemindProjection user : users){
            mailService.sendStartSubjectRemind(user.getEmail(), user.getFirst_name(), user.getTitle());
        }
    }
}