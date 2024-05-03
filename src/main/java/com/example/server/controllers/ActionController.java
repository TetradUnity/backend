package com.example.server.controllers;

import com.example.server.entities.UserEntity;
import com.example.server.models.EditedUser;
import com.example.server.models.Role;
import com.example.server.models.User;
import com.example.server.repositories.UserEntityRepository;
import com.example.server.services.CheckValidDataService;
import com.example.server.services.ResponseService;
import com.example.server.utils.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ActionController {

	@Autowired
	UserEntityRepository userEntityRepository;

	private static PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

	@GetMapping("/profile")
	public ResponseEntity<Object> getInfo(HttpServletRequest req){
		UserEntity user = AuthUtil.authorizedUser(req);

		if(user == null){
			return ResponseService.unauthorized();
		}

		return ResponseEntity.ok().body(new User(user));
	}
	@DeleteMapping("/delete-user")
	public ResponseEntity<Object> deleteUser(HttpServletRequest req, @RequestBody UserEntity userEmail){
		UserEntity user = AuthUtil.authorizedUser(req);

		if(user == null){
			return ResponseService.failed();
		}

		String email = userEmail.getEmail();
		UserEntity userInfo = (userEntityRepository.findByEmail(email)).orElse(null);

		if(userInfo == null){
			return ResponseService.failed();
		}

		if((user.getRole() == Role.teacher && userInfo.getRole() == Role.student) ||
				(user.getRole() == Role.chief_teacher && userInfo.getRole().ordinal() < 2)){
			userEntityRepository.delete(userInfo);
			Map<String, Object> response = new HashMap<>();
			response.put("successful", true);
			return ResponseEntity.ok().body(response);
		}
		return ResponseService.failed("no_permission");
	}
	@PutMapping("/edit-profile")
	public ResponseEntity<Object> editProfile(HttpServletRequest req, @RequestBody EditedUser editedUser){
		UserEntity user = AuthUtil.authorizedUser(req);

		if(user == null){
			return ResponseService.failed();
		}

		if(editedUser == null){
			return ResponseService.failed();
		}

		String oldPassword = editedUser.getOldPassword();
		if(oldPassword == null){
			return ResponseService.failed();
		}

		UserEntity newUserInfo = editedUser.getNewUserInfo();

		if(newUserInfo == null){
			return ResponseService.failed();
		}

		if(passwordEncoder.matches(oldPassword, user.getPassword())){
			String email = newUserInfo.getEmail();
			email = (email == null) ? user.getEmail() : email;

			String password = newUserInfo.getPassword();
			password = (password == null) ? user.getPassword() : password;

			String first_name = newUserInfo.getFirst_name();
			first_name = (first_name == null) ? user.getFirst_name() : first_name;

			String last_name = newUserInfo.getLast_name();
			last_name = (last_name == null) ? user.getLast_name() : last_name;

			boolean checkExists = !(email.equals(user.getEmail()));

			String validData = CheckValidDataService.checkUser(email, password, first_name, last_name, checkExists);

			if(!validData.equals("ok")){
				return ResponseService.failed(validData);
			}

			user.setEmail(newUserInfo.getEmail());
			user.setPassword(newUserInfo.getPassword() != null ? passwordEncoder.encode(newUserInfo.getPassword()) : null);
			user.setFirst_name(newUserInfo.getFirst_name());
			user.setLast_name(newUserInfo.getLast_name());


			userEntityRepository.save(user);
			Map<String, Object> response = new HashMap<>();
			response.put("successful", true);
			return ResponseEntity.ok().body(response);
		}
		return ResponseService.failed("incorrect_password");
	}
}