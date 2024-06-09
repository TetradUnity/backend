package com.tetradunity.server.repositories;

import com.tetradunity.server.entities.PasswordRecoveryRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface PasswordRecoveryRequestRepository extends JpaRepository<PasswordRecoveryRequest, Long> {
    Optional<PasswordRecoveryRequest> findByUid(UUID uid);
    Optional<PasswordRecoveryRequest> findByEmail(String email);
}