package com.tetradunity.server.utils;

import com.tetradunity.server.entities.RefreshToken;
import com.tetradunity.server.entities.UserEntity;
import com.tetradunity.server.repositories.RefreshTokenRepository;
import com.tetradunity.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
public class RefreshTokenUtil {

	static private RefreshTokenRepository refreshTokenRepository;
	static private UserRepository userRepository;

	@Autowired
	public RefreshTokenUtil(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository){
		RefreshTokenUtil.refreshTokenRepository = refreshTokenRepository;
		RefreshTokenUtil.userRepository = userRepository;
	}

	public static RefreshToken createRefreshToken(UserEntity user){
		Optional<RefreshToken> refreshTokenExists = refreshTokenRepository.findByUserEntity(user);
		if(refreshTokenExists.isEmpty()){
			RefreshToken refreshToken = RefreshToken.builder()
					.userEntity(user)
					.token(UUID.randomUUID().toString())
					.expiryDate(Instant.now().plusMillis(86_400_000))
					.build();
			return refreshTokenRepository.save(refreshToken);
		}
		return refreshTokenExists.get();
	}

	public static RefreshToken verifyExpiration(String token){
		RefreshToken refreshToken = (refreshTokenRepository.findByToken(token)).orElse(null);

		if(refreshToken == null){
			return null;
		}

		refreshTokenRepository.delete(refreshToken);
		if(refreshToken.getExpiryDate().compareTo(Instant.now())<0){
			return null;
		}
		return refreshToken;
	}
}
