package com.tetradunity.server.utils;

import com.tetradunity.server.entities.RefreshToken;
import com.tetradunity.server.entities.UserEntity;
import com.tetradunity.server.models.UserWithTokens;
import com.tetradunity.server.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {
	private static UserRepository userRepository;

	@Autowired
	public AuthUtil(UserRepository userRepository) {
		AuthUtil.userRepository = userRepository;
	}

	public static UserEntity authorizedUser(HttpServletRequest req) {
		String token = req.getHeader(HttpHeaders.AUTHORIZATION);

		if (token == null) {
			return null;
		}

		Long userId;

		try{
			userId = Long.parseLong(JwtUtil.extract(token));
		}catch(Exception e){return null;}

		return userRepository.findById(userId).orElse(null);
	}

	public static UserWithTokens refreshAuthorizedUser(HttpServletRequest req){
		String token = req.getHeader(HttpHeaders.AUTHORIZATION);

		if(token == null){
			return null;
		}

		RefreshToken refreshToken = RefreshTokenUtil.verifyExpiration(token);
		if(refreshToken != null){
			UserEntity userEntity = refreshToken.getUserEntity();
			return new UserWithTokens(
					userEntity,
					JwtUtil.generateToken(String.valueOf(userEntity.getId())),
					RefreshTokenUtil.createRefreshToken(userEntity)
			);
		}
		return null;
	}
}