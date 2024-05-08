package com.tetradunity.server.controllers;

import com.tetradunity.server.entities.UserEntity;
import com.tetradunity.server.models.Role;
import com.tetradunity.server.models.UserWithTokens;
import com.tetradunity.server.repositories.UserRepository;
import com.tetradunity.server.services.CheckValidDataService;
import com.tetradunity.server.services.ResponseService;
import com.tetradunity.server.utils.AuthUtil;
import com.tetradunity.server.utils.JwtUtil;
import com.tetradunity.server.utils.RefreshTokenUtil;
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

	@GetMapping("create-admin")
	public ResponseEntity<Object> method(){
		UserEntity user = new UserEntity("maksimmankovskiy04@gmail.com",
				passwordEncoder.encode("qwerty")
				, "Maks", "Mankov", Role.chief_teacher);
		userRepository.save(user);
		Map<String, Object> response = new HashMap<>();
		response.put("ok", true);
		return ResponseEntity.ok().body(response);
	}

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

		if(user.getRole() == Role.chief_teacher){
			String email = newUserInfo.getEmail();
			String password = newUserInfo.getPassword();
			String first_name = newUserInfo.getFirst_name();
			String last_name = newUserInfo.getLast_name();
			Role role = newUserInfo.getRole();

			if(role != Role.teacher && role != Role.student){
				ResponseService.failed();
			}

			String validData = CheckValidDataService.checkUser(email, password, first_name, last_name, true);

			if(!validData.equals("ok")){
				return ResponseService.failed(validData);
			}

			UserEntity newUser = new UserEntity(
					email, passwordEncoder.encode(password),
					first_name, last_name, role
			);
			userRepository.save(newUser);
			Map<String, Object> response = new HashMap<>();
			response.put("ok", true);
			return ResponseEntity.ok().body(response);
		}
		return ResponseService.failed("no_permission");
	}
}