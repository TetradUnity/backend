package com.tetradunity.server.controllers;

import com.tetradunity.server.entities.UserEntity;
import com.tetradunity.server.models.EditedUser;
import com.tetradunity.server.models.Role;
import com.tetradunity.server.models.User;
import com.tetradunity.server.repositories.UserRepository;
import com.tetradunity.server.services.CheckValidDataService;
import com.tetradunity.server.services.ResponseService;
import com.tetradunity.server.utils.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	UserRepository userRepository;

	private static PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

	@GetMapping("/get")
	public ResponseEntity<Object> getInfo(HttpServletRequest req,
										  @RequestParam(name = "id", required = false, defaultValue = "0") long id){
		UserEntity user = AuthUtil.authorizedUser(req);

		if(user == null){
			return ResponseService.unauthorized();
		}

		Map<String, Object> response = new HashMap<>();

		if(id != 0){
			user = userRepository.findById(id).orElse(null);
			if(user == null){
				return ResponseService.failed("user_not_found", HttpStatus.NOT_FOUND);
			}
		}

		response.put("ok", true);
		response.put("user", new User(user));

		return ResponseEntity.ok().body(response);
	}
	@DeleteMapping("/delete")
	public ResponseEntity<Object> deleteUser(HttpServletRequest req, @RequestBody long id){
		UserEntity user = AuthUtil.authorizedUser(req);

		if(user == null){
			return ResponseService.failed();
		}

		UserEntity userInfo = (userRepository.findById(id)).orElse(null);

		if(userInfo == null){
			return ResponseService.failed();
		}

		if(user.getRole() == Role.chief_teacher && userInfo.getRole().ordinal() < 2){
			userRepository.delete(userInfo);
			Map<String, Object> response = new HashMap<>();
			response.put("ok", true);
			return ResponseEntity.ok().body(response);
		}
		return ResponseService.failed("no_permission");
	}
	@PutMapping("/edit")
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


			userRepository.save(user);
			Map<String, Object> response = new HashMap<>();
			response.put("ok", true);
			return ResponseEntity.ok().body(response);
		}
		return ResponseService.failed("incorrect_password");
	}
}