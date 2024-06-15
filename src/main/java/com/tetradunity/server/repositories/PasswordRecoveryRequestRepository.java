package com.tetradunity.server.repositories;

import com.tetradunity.server.entities.PasswordRecoveryRequest;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface PasswordRecoveryRequestRepository extends JpaRepository<PasswordRecoveryRequest, Long> {
    Optional<PasswordRecoveryRequest> findByUid(UUID uid);
    Optional<PasswordRecoveryRequest> findByEmail(String email);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM password_recovery_requests pr WHERE pr.expiration < UNIX_TIMESTAMP() * 1000", nativeQuery = true)
    void deleteByExpiryDateBefore();
}