package com.tetradunity.server.repositories;

import com.tetradunity.server.entities.RefreshToken;
import com.tetradunity.server.entities.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	Optional<RefreshToken> findByToken(String token);
	Optional<RefreshToken> findByUserEntity(UserEntity userEntity);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM refresh_tokens rt WHERE rt.expiry_date < :expiry_date", nativeQuery = true)
	void deleteByExpiryDateBefore(Instant expiry_date);
}