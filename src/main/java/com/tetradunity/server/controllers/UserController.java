package com.tetradunity.server.controllers;

import com.tetradunity.server.entities.UserEntity;
import com.tetradunity.server.models.EditedUser;
import com.tetradunity.server.models.Role;
import com.tetradunity.server.models.User;
import com.tetradunity.server.repositories.UserRepository;
import com.tetradunity.server.services.CheckValidService;
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
import java.util.List;
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

		if(id != 0 && id != user.getId()){
			Role myRole = user.getRole();
			user = userRepository.findById(id).orElse(null);
			switch (user.getRole()){
				case student:
					if(user.getRole() == Role.student){
						user.setEmail(null);
					}
				case teacher:
				case chief_teacher:
					if(user == null){
						return ResponseService.failed("user_not_found", HttpStatus.NOT_FOUND);
					}
					break;
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

		String email = editedUser.getEmail();
		String first_name = editedUser.getFirst_name();
		String last_name = editedUser.getLast_name();
		String password = editedUser.getPassword();
		String oldPassword = editedUser.getOldPassword();

		if(password != null){
			if(passwordEncoder.matches(oldPassword, user.getPassword())){
				user.setPassword(password);
			}
			else{
				return ResponseService.failed("incorrect_password");
			}
		}

		if(email != null){
			user.setEmail(email);
		}

		if(first_name != null){
			user.setEmail(email);
		}

		if(last_name != null){
			user.setEmail(email);
		}

		userRepository.save(user);

		Map<String, Object> response = new HashMap<>();
		response.put("ok", true);
		return ResponseEntity.ok().body(response);
	}

	@GetMapping("getOptions")
	public ResponseEntity<Object> getOptions(HttpServletRequest req, @RequestBody User user) {
		UserEntity me = AuthUtil.authorizedUser(req);

		if (me == null) {
			return ResponseService.failed();
		}

		if (me.getRole() != Role.chief_teacher) {
			return ResponseService.failed("no_permission");
		}

		if (user.getEmail() == null || user.getRole() == null || user.getEmail().length() < 2) {
			return ResponseService.failed();
		}

		List<UserEntity> users = userRepository.findByEmailPrefixAndRole(user.getEmail(), user.getRole());

		Map<String, Object> response = new HashMap<>();
		response.put("ok", true);
		response.put("users", users);
		return ResponseEntity.ok().body(response);
	}
}