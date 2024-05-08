package com.tetradunity.server.repositories;

import com.tetradunity.server.entities.RefreshToken;
import com.tetradunity.server.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
	Optional<RefreshToken> findByToken(String token);
	Optional<RefreshToken> findByUserEntity(UserEntity userEntity);
}