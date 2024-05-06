package com.example.server.controllers;

import com.example.server.entities.UserEntity;
import com.example.server.models.Role;
import com.example.server.models.UserWithTokens;
import com.example.server.repositories.UserRepository;
import com.example.server.services.CheckValidDataService;
import com.example.server.services.ResponseService;
import com.example.server.utils.AuthUtil;
import com.example.server.utils.JwtUtil;
import com.example.server.utils.RefreshTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/authorization")
public class AuthController {

	@Autowired
    UserRepository userRepository;

	private static PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

	@PostMapping("/refresh-authorized")
	public ResponseEntity<Object> refreshAuthorized(HttpServletRequest req){
		UserWithTokens userWithTokens = AuthUtil.refreshAuthorizedUser(req);
		if(userWithTokens == null){
			return null;
		}
		Map<String, Object> response = new HashMap<>();
		response.put("ok", true);
		response.put("accessToken", userWithTokens.getAccessToken());
		response.put("refreshToken", userWithTokens.getRefreshToken());
		return ResponseEntity.ok().body(response);
	}

	@PostMapping("/login")
	public ResponseEntity<Object> login(@RequestBody UserEntity userInfo){
		if(userInfo == null){
			return ResponseService.failed();
		}
		if(userInfo.getEmail() == null || userInfo.getPassword() == null){
			return ResponseService.failed();
		}

		UserEntity user = userRepository.findByEmail(userInfo.getEmail()).get();

		if(user == null){
			return ResponseService.failed();
		}

		if(passwordEncoder.matches(userInfo.getPassword(), user.getPassword())){
			Map<String, Object> response = new HashMap<>();
			response.put("ok", true);
			response.put("accessToken", JwtUtil.generateToken(String.valueOf(user.getId())));
			response.put("refreshToken", RefreshTokenUtil.createRefreshToken(user).getToken());
			return ResponseEntity.ok().body(response);
		}
		return ResponseService.failed();
	}

	@PostMapping("/create-user")
	public ResponseEntity<Object> createTeacher(HttpServletRequest req, @RequestBody UserEntity newUserInfo){
		UserEntity user = AuthUtil.authorizedUser(req);

		if(user == null){
			return ResponseService.unauthorized();
		}

		if(user.getRole() == Role.chief_teacher || user.getRole() == Role.teacher){
			String email = newUserInfo.getEmail();
			String password = newUserInfo.getPassword();
			String first_name = newUserInfo.getFirst_name();
			String last_name = newUserInfo.getLast_name();

			String validData = CheckValidDataService.checkUser(email, password, first_name, last_name, true);

			if(!validData.equals("ok")){
				return ResponseService.failed(validData);
			}

			UserEntity newUser = new UserEntity(
					email, passwordEncoder.encode(password), first_name, last_name,
					(user.getRole() == Role.chief_teacher) ? Role.teacher : Role.student
			);
			userRepository.save(newUser);
			Map<String, Object> response = new HashMap<>();
			response.put("ok", true);
			return ResponseEntity.ok().body(response);
		}
		return ResponseService.failed("no_permission");
	}
}