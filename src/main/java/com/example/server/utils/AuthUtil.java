package com.example.server.utils;

import com.example.server.entities.RefreshToken;
import com.example.server.entities.UserEntity;
import com.example.server.models.UserWithTokens;
import com.example.server.repositories.UserEntityRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {
	private static UserEntityRepository userEntityRepository;

	@Autowired
	public AuthUtil(UserEntityRepository userEntityRepository) {
		AuthUtil.userEntityRepository = userEntityRepository;
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

		return userEntityRepository.findById(userId).orElse(null);
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